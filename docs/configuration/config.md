# The Configuration File

---

## Messages

Messages can have text and embeds.

### Text

Message text can be defined with the `text` parameter.
It can either be provided as a string, or as a list that will be flattened and separated by `\n`.

Example configuration:

```yaml
response:
  text: |-
    You can **format** this text just like you can in Discord :sunglasses:
```

### Embeds

Messages also support sending embeds.

Example configuration:

```yaml
response:
  embeds:
  - author:
      name: Chat GPT
      icon: https://imgur.com/Bd5fxNG.png
    title: The best
    description: |-
      I know more about programming than anybody, believe me. 
      It's tremendous, just tremendous. The best code you've ever seen, trust me.
```

Embeds support all parameters except for fields.

## Emojis

When defining emojis in the configuration file, you can choose from Unicode emojis, or custom emojis.
Both of which use a very similar syntax.

### Custom Emojis

- `name` - The name of the emoji.
- `id` - The ID of the emoji.
- `animated` - Whether the emoji is animated. Defaults to `false`.

Example configuration:

```yaml
reactions:
  - name: pepebigsmile
    id: 938133767682420767
    animated: false # Defaults to false, not required
```

### Unicode Emojis

- `name` - The emoji.

Example configuration:

```yaml
reactions:
  - name: 🗿
```
