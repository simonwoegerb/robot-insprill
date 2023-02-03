# Configuring the development environment

---

To begin working on Robot Insprill, there's a little setup you'll have to do.

## Environment Variables

Running Robot Insprill requires a couple environment variables to be set.
Check out the [Environment Variables](../configuration/env.md) section on which ones need to be set.

In addition to those, you'll also need to set `CONFIG_FILE`.
This is the relative path to the bot's config.
Set it to `config/dev.yml` to use the default development config.
If unset, it will try to use `config.yml` in the root of the project.

## Configuration

You'll also have to tweak some settings in the configuration file.  
The `dev.yml` file is ignored by git, so you don't have to worry about accidentally committing changes to it.

The following IDs will have to be updated to the Discord server you use for testing:

- `guild`
- `audit.channel`
- `statistic-channels.channel-id` (if uncommented)
