# Emojis

---

Some config parameters specify that they take an emoji.
When you see that, this is what they're referring to.

In the following examples, `response` takes an emoji.

## Format

When defining emojis in the configuration file, you can choose from Unicode emojis, or custom emojis.
Both of which use a very similar syntax.

### Custom Emojis

- `name` - The name of the emoji.
- `id` - The ID of the emoji.
- `animated` - Whether the emoji is animated. Must be set correctly. Defaults to `false`.

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
