# Environment Variables

---

Running Robot Insprill requires a couple environment variables to be set.

During developing, if you're using IntelliJ, you can set them in the run configuration.  
When running in production, you can set them in your `docker-compose.yml` or in your startup script.

- `DISCORD_TOKEN` (required) - The token used for your bot.
- `PASTEBIN_API_KEY` - Your [Pastebin API key](https://pastebin.com/doc_api#1).
  You must be logged in to retrieve it.
  Only required when uploading bins to Pastebin.
- `YOUTUBE_API_KEY` - A [Google API key](https://support.google.com/googleapi/answer/6158862) with access to the YouTube
  Data API v3. Only required when using YouTube statistic channels.
