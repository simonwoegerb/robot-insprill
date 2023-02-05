# Statistic Channels

---

Statistic channels are channels that exist to display a certain statistic.

## Format

- `channelId` - The ID of the channel to use. Must be a voice channel!
- `format` - The format of the channel name. Must contain `%s` which will be replaced with the statistic.
- `statistic` - The statistic to use. Supports the following values:
    - `YOUTUBE_SUBS` - Amount of YouTube subscribers the provided channel has.
      Requires the `YOUTUBE_API_KEY` environment variable to be set.
      Requires `data` to be set to the channel ID.
    - `YOUTUBE_VIEW` - Amount of YouTube views the provided channel has.
      Requires the `YOUTUBE_API_KEY` environment variable to be set.
      Requires `data` to be set to the channel ID.

### Example Configuration
```yaml
statistic-channels:
  - channel-id: 821976647863238696
    format: "CodedRed Subs: %s"
    statistic: YOUTUBE_SUBS
    data: UC_kPUW3XPrCCRT9a4Pnf1Tg
```
