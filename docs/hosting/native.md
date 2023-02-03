# Native

---

To get started, you'll need to clone this repository and cd into it.

```shell
git clone https://github.com/Insprill/robot-insprill
cd robot-insprill
```

## Building

To build Robot Insprill, run the following command

```shell
./gradlew build
```

## Installing Dependencies

To run Robot Insprill natively, you'll need a few dependencies.
On Ubuntu-based distro's, you can install them with the following command:

```shell
sudo apt update && sudo apt install openjdk-17-jdk-headless screen
```

This will install Java 17, and [screen](https://help.ubuntu.com/community/Screen).

## Startup Script

To make managing your instance easier, you should create a startup script.
You can use this as a template:

```shell
export DISCORD_TOKEN=""
export PASTEBIN_API_KEY=""
export YOUTUBE_API_KEY=""
screen -d -m -S "robot-insprill" java -jar build/libs/robot-insprill.jar
```

The file name may not be correct depending on the version of Robot Insprill you're building.
You can check its name with the following command

```shell
ls build/libs
```

For more information on the environment variables, check out the [Environment Variables docs](../configuration/env.md).
If you'd like, you can move the environment variables to a file called `.env`, and replace them with `source .env` in
the startup script.

## Configuration File

For a good config template, you can copy the development config.

```shell
cp configs/dev.yml config.yml
```

Now you can open it with your favorite text editor and configure until your heart's content.
