# Messages

---

Some config parameters specify that they take a message.
When you see that, this is what they're referring to.

In the examples, `response` *takes* a message.
The actual message does not include `response` in it.

## Format

All parameters are optional.

- `text` - The text part of the message, as if you typed it in Discord.
- `embeds` - A list of [embeds](#embeds) to send.

### Embeds

All parameters are optional.

- `author` - The embed's [author](#author).
- `title` - The message title.
- `url` - The URL to open when the title is clicked.
- `description` - The description of the embed.
- `image` - URL of the image displayed by the embed.
- `thumbnail` - URL of embed's thumbnail.
- `color`- The color of the embed, as an RGB encoded HEX string (standard HEX format).
- `footer`- The embed's [footer](#footer).
- `timestamp` - The timestamp displayed next to the embed footer,
  in [Unix Millis](https://en.wikipedia.org/wiki/Unix_time).
  [Simple date to Unix time converter](https://www.epochconverter.com/)

#### Author

All parameters are optional.

- `name` - The authors name.
- `url` - The URL to open when the author's name is clicked.
- `icon` - URL of the author's icon.

#### Footer

All parameters are optional unless stated otherwise.

- `text` (Required) - The text of the footer.
- `icon` - URL of the footer icon.

### Example Configuration:

In this example, `response` takes a message.
This does not use all parameters, but should give you an idea of what to write.

```yaml
response:
  text: Hello there!
  embeds:
  - author:
      name: Chat GPT
      icon: https://imgur.com/Bd5fxNG.png
    title: Purple Elephant
    description: The purple elephant danced gracefully on the tightrope while juggling pineapples.
```
