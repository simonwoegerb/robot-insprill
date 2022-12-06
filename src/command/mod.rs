use std::env;

use serenity::async_trait;
use serenity::model::application::interaction::{Interaction, InteractionResponseType};
use serenity::model::gateway::Ready;
use serenity::model::id::GuildId;
use serenity::prelude::*;
use tracing::log::{error, info};

pub mod binfile;

pub struct Handler;

#[async_trait]
impl EventHandler for Handler {
    async fn interaction_create(&self, ctx: Context, interaction: Interaction) {
        if let Interaction::ApplicationCommand(command) = interaction {
            let content = match command.data.name.as_str() {
                "binfile" => binfile::run(&command.data.options),
                _ => "not implemented :(".to_string(),
            };

            if let Err(err) = command
                .create_interaction_response(&ctx.http, |response| {
                    response
                        .kind(InteractionResponseType::ChannelMessageWithSource)
                        .interaction_response_data(|message| message.content(content))
                })
                .await
            {
                error!("Failed to respond to slash command: {}", err);
            }
        }
    }

    async fn ready(&self, ctx: Context, _: Ready) {
        let guild_id = GuildId(
            env::var("GUILD_ID")
                .expect("Expected GUILD_ID in environment")
                .parse()
                .expect("GUILD_ID must be an integer"),
        );

        let commands = GuildId::set_application_commands(&guild_id, &ctx.http, |commands| {
            commands.create_application_command(|command| binfile::register(command))
        })
        .await;

        match commands {
            Ok(cmds) => {
                info!("Registered {:?} slash commands", cmds.len());
            }
            Err(err) => {
                error!(
                    "An error occured while registering slash commands: {:?}",
                    err
                );
            }
        }
    }
}
