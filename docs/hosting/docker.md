# Docker

---

Docker and Docker Compose is the easier way to get started.

Firstly, you'll need to clone this repository and cd into it.

```shell
git clone https://github.com/Insprill/robot-insprill
cd robot-insprill
```

This is because Robot Insprill isn't on Docker Hub, so you'll need to build it from source.

## Configuration File

For a good config template, you can copy the development config.

```shell
cp configs/dev.yml config.yml
```

Now you can open it with your favorite text editor and configure until your heart's content.

## Docker Compose

Now open the `docker-compose.yml` with your favorite text editor, and fill in the environment variables.
For more information on them, check out the [Environment Variables docs](../configuration/env.md).
If you'd like, you can move the environment variables to a file called `.env`.
For more information on that, you can check out
the [Docker docs](https://docs.docker.com/compose/environment-variables/env-file/).

## Running the Bot
To start the bot, run the following command
```shell
docker compose up -d
```

For more information on Docker and Docker Compose, [NetworkChuck](https://www.youtube.com/@NetworkChuck) has [a great playlist](https://www.youtube.com/playlist?list=PLIhvC56v63IJlnU4k60d0oFIrsbXEivQo) explaining how it works.
