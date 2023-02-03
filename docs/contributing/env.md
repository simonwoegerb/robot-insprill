# Configuring the development environment

---

To begin working on Robot Insprill, there's a little setup you'll have to do.

## Environment Variables

Running Robot Insprill requires a couple environment variables to be set.
Check out the [Environment Variables](../configuration/env.md) section on which ones need to be set.

## Configuration

You'll also have to tweak some settings in the configuration file.
The `dev.yml` config is intended to be used as a template, and to be used during development.
When you run the bot for the first time, it will copy the `configs/dev.yml` file to `./config.yml`.
The `config.yml` file is ignored by git, so you don't have to worry about accidentally committing it.

The following IDs will have to be updated to the Discord server you use for testing:

- `guild`
- `audit.channel`
- `statistic-channels.channel-id` (if uncommented)
