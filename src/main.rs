use std::env;

use serenity::async_trait;
use serenity::model::gateway::Ready;
use serenity::prelude::*;
use tracing::{error, info};

pub mod command;

pub struct Handler;

#[async_trait]
impl EventHandler for Handler {
    async fn ready(&self, _: Context, ready: Ready) {
        info!("Successfully logged into {}", ready.user.tag());
    }
}

#[tokio::main]
async fn main() {
    dotenv::dotenv().expect("Failed to load .env file");

    tracing_subscriber::fmt::init();

    let token = env::var("DISCORD_TOKEN").expect("env variable `DISCORD_TOKEN` should be set");

    let intents = GatewayIntents::GUILD_MESSAGES | GatewayIntents::MESSAGE_CONTENT;

    let mut client = Client::builder(&token, intents)
        .event_handler(Handler)
        .event_handler(command::Handler)
        .await
        .expect("Err creating client");

    info!("Starting {}", env!("CARGO_PKG_NAME"));

    let shard_manager = client.shard_manager.clone();

    tokio::spawn(async move {
        tokio::signal::ctrl_c()
            .await
            .expect("Could not register ctrl+c handler");
        shard_manager.lock().await.shutdown_all().await;
    });

    if let Err(why) = client.start().await {
        error!("Caught client error: {:?}", why);
    }
}
