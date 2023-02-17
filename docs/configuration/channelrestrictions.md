# Channel Restrictions

---

When a user send a message without the required types in a channel that is restricted, the bot will delete the message and respond with a message.


## Configuration
Channel Restrictions have the following parameters:

- `channel-id` - The channel ID of the restricted channel.
- `message` - A [message](props/messages.md) to respond with.
- `types` - A list of allowed content types. The following types are supported:
    - `TEXT` - Any Message that contains a character (including spaces).
    - `LINK` - Any message that contains "http".
    - `ATTACHMENT` - An attachment is a file that is not an image or video.
    - `IMAGE` - File types: (png, jpg, jpeg, gif, webp)
    - `VIDEO` - File types: (mp4, webm, mov)

### Example Configuration
```yaml
restricted-channels:
    - channel-id: 1075925445440180265
      message:
          text: |-
              This channel is restricted to Memes only. (Images, Videos, and Links)
      types:
          - IMAGE
          - VIDEO
          - LINK
    - channel-id: 796348801061093396
      message:
          text: |-
              This channel is restricted to sharing artwork. (Images and Videos)
      types:
          - IMAGE
          - VIDEO
```
