# CI workflows (staged here on purpose)

These are the stock Morphe release workflows from `morphe-patches-template`.
They live under `ci/workflows/` instead of `.github/workflows/` only because the
repo was first pushed with an OAuth token lacking the GitHub `workflow` scope,
which refuses to create files under `.github/workflows/`.

**To activate automated releases**, a maintainer with a `workflow`-scoped token runs:

```bash
git mv ci/workflows/release.yml           .github/workflows/release.yml
git mv ci/workflows/open_pull_request.yml .github/workflows/open_pull_request.yml
git commit -m "ci: enable release workflows"
git push     # token must have the `workflow` scope (gh auth refresh -s workflow)
```

Then, in repo Settings → Actions → General → Workflow permissions, enable
"Allow GitHub Actions to create and approve pull requests", and add the Morphe
registry PAT the workflow expects (see `CONTRIBUTING.md`). Do not edit generated
files (`patches-list.json`, `patches-bundle.json`, `CHANGELOG.md`) by hand.
