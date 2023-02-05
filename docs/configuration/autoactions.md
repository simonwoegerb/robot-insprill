# Automatic Actions

---

Automatic actions can be performed when a message is sent that matches a regex pattern.
It can scan text in messages, files, bin websites (like Pastebin), and from images.


## Configuration
Auto actions have the following parameters:

- `channels` - A list of channel ID's to scan. If not specified, all channels will be scanned. For threads and forums, it uses the parent channel's ID.
- `media` - A list of media types to scan. The following types are supported:
    - `TEXT` - The content of a message.
    - `FILE` - The contents of a text file. Supports mime types `text/*` and `application/*`.
    - `BIN` - The contents of a bin link, like Pastebin.
    - `IMAGE` - The text contents of an image. This can be slow and resource intensive. Only use if required.
- `bots` - Whether bot messages should be scanned. Defaults to `false`.
- `actions` - A list of actions. See below.

Actions have three parameters:

- `pattern` - The regex pattern to match. The entire string won't be matched against the pattern, but it will instead check if any of the content matches.
- `reactions` - A list of [reactions](props/emojis.md) to react to the message with.
- `responses` - A list of [messages](props/messages.md) to respond with.

### Example Configuration
```yaml
auto-actions:
  - channels:
      - 1071721704302313512
    media:
      - TEXT
      - FILE
      - BIN
      - IMAGE
    bots: true
    actions:
      - pattern: "(?i)big ?boy"
        responses:
          - text: NO BIG BOY
        reactions:
          - name: 🚂
```
