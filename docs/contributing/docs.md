# Building Documentation

---

To build Robot Insprill's docs, you'll need to have pip and python3 installed.

- [How to install Python](https://wiki.python.org/moin/BeginnersGuide/Download)
- [How to install pip](https://pip.pypa.io/en/stable/installation/)

Our docs require two pip packages to build, `mkdocs` and `mkdocs-material`.  
You can install them with the following command:
```shell
pip install mkdocs mkdocs-material
```
To server the documentation locally, you can run
```shell
mkdocs serve
```

It can then be accessed at [http://127.0.0.1:8000/](http://127.0.0.1:8000/).

For information on how MkDocs works, check out [their documentation](https://www.mkdocs.org/getting-started/#adding-pages).
