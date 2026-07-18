# Frontend du PoC de tchat

Les instructions complètes se trouvent dans le
[`README.md`](../README.md) à la racine du repository.

Commandes principales :

```powershell
npm.cmd install
npm.cmd start
npm.cmd test -- --watch=false
npm.cmd run build
```

`npm.cmd start` utilise `proxy.conf.json` pour transmettre `/api` et `/ws` au
backend local sur le port `8080`.
