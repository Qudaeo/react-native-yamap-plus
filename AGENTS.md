# AGENTS.md

React Native binding for Yandex MapKit SDK — a fork of `react-native-yamap`, New Architecture only (Fabric + TurboModules + codegen `RNYamapPlusSpec`).

## Setup commands

- Install deps: `yarn install` (Yarn 4.18.0, `nodeLinker: node-modules`, workspace root)
- Node: `>= 22.11.0` (CI uses 24); enable Corepack first
- Typecheck/build: `yarn build` (tsc → `lib/`) — clean, 0 errors
- Lint: `npx eslint src --ext .ts,.tsx`
- Run example app: `yarn example start` / `yarn example ios` / `yarn example android`
- Native builds: `yarn example build:ios` / `yarn example build:android`
- Release: `yarn release <patch|minor|major>` via `.github/workflows/release.yml` (manual dispatch only)

**Single-copy invariant:** there must be exactly ONE `react` and ONE `react-native` on disk, both in `example/node_modules`. Do **not** add them (or anything pulling them in) as root `devDependencies` — yarn will hoist a second copy to root `node_modules`, Metro then bundles two Reacts, and the app dies at runtime with `Invalid hook call` / `Cannot read property 'useRef' of null` (`example/metro.config.js` relies on this: it aliases every `peerDependencies` entry into `example/node_modules`). Keep the root `peerDependencies` as-is.

**Typecheck types:** root `tsc` cannot resolve `react-native` from `src/` on its own, because the only copy lives under `example/`. Both `tsconfig.json` (root) and `example/tsconfig.json` map it explicitly via `paths` → `./node_modules/react-native/types_generated/index.d.ts`. Don't add `@types/react-native`: RN >= 0.71 ships its own types. `react` needs no mapping — `@types/react` (a root devDep) covers it. If typecheck suddenly reports `Cannot find module 'react-native'`, those two `paths` entries are the cause. Root `tsconfig.json` is deliberately **not** in the npm `files` field — it must not be published, or that `paths` entry ships as a dangling reference to a directory that isn't in the tarball (`example/` is excluded).

## Project layout

- `src/` — TypeScript library source (`main`/`source` = `src/index.ts`; the published package ships sources, not `lib/`)
- `src/components/` — React components: `Yamap`, `ClusteredYamap`, `Marker`, `Circle`, `Polygon`, `Polyline`
- `src/spec/` — codegen specs: Fabric component specs, TurboModule specs, `commands/` (imperative native commands)
- `src/modules/` — TurboModule wrappers: `Search`, `Suggest`, `Transport`, `YamapInstance`
- `src/hooks/useYamap.ts` — shared imperative handle for `Yamap` and `ClusteredYamap`
- `src/utils/` — `CallbacksManager` (guid one-shot callbacks), `guid`, prop helpers
- `android/src/main/java/ru/yamap/` — Kotlin views, view managers, modules, codegen events
- `android/src/mapkit/{full,lite}/` — variant-specific Search/Suggest/Transport impls, selected by the `useYandexMapsLite` Gradle property
- `ios/{View,Module,Util}/` — Objective-C++ (`.mm`), Fabric view managers + TurboModules
- `RNYamap.podspec`, `app.plugin.js` — iOS podspec and Expo config plugin
- `example/` — demo app workspace, the only place real runtime verification happens

## Adding or changing native API

A change almost always spans four layers — update all of them:

1. `src/spec/*NativeComponent.ts` / `Native*Module.ts` / `spec/commands/*` (codegen contract)
2. `package.json` → `codegenConfig` (`componentProvider` for new iOS Fabric components, `jsSrcsDir`, `javaPackageName`)
3. Android: `view/*ViewManager*.kt` + `events/` + `module/`
4. iOS: `ios/View/*.mm` (RCTViewComponentView subclass) + `ios/Module/*.mm`

Native props use `CodegenTypes.*`, not raw types. Omit `children` — render custom markers as snapshots passed through `source` (see the deprecated `children` prop in `Marker.tsx`).

**Callbacks pattern:** command takes a callback → `CallbacksManager.addCallback` returns a guid → native command carries `{id}` → native emits an event with the same `id` → `CallbacksManager.call(id, ...)` fires and deletes it. Follow this for any new one-shot async command.

**MapKit SDK version is pinned in two places** — `RNYamap.podspec` (4.42.0) *and* `android/gradle.properties` (`YamapPlus_mapsMobileFullVersion` / `YamapPlus_mapsMobileLiteVersion`). Bump both in one commit; the repo history shows them changed as a pair.

## Code style

- TypeScript `strict: true`, `verbatimModuleSyntax: true` — use `import type {X}` / inline `{type X}`, never plain `import {type X}`-only-value imports
- 2-space indent, single quotes, trailing commas (see `example/.prettierrc.js`)
- ESLint: root `.eslintrc.js`, `@react-native` config, `lib/` ignored
- Barrel files (`components/index.ts`, `modules/index.ts`, `utils/index.ts`) re-export public API — keep them in sync when adding a module
- Public API surface is re-exported from `src/index.ts`; document user-facing props in `README.md` (written in Russian)

## Testing instructions

- No test suite exists — there is no test script and no `*.test.*` / `__tests__` files (Jest preset exists in `example` but is unused)
- Verify changes by running the example app: `yarn example ios` / `yarn example android`
- Native change checklist: codegen specs match native managers, prop types align across TS/Kotlin/ObjC, both Full and Lite variants still compile, `npx eslint src` is clean
- Public API changes require a `README.md` update

## PR & commit conventions

- Branch from `main`; never push directly to `main`
- Conventional commits with scopes, as used in history: `feat:`, `fix:`, `chore:`, `chore(example):`, `chore(ts):`, `fix(ios):`, `fix(ci):`, `fix(android):`
- Example: `feat: add focusInsets prop for the camera focus rect`
- Release commits (`chore: release vX.Y.Z`) are generated by release-it — don't write them by hand
- Release requires a manual `workflow_dispatch` on `main` with `NPM_TOKEN`; never bypass it

## Security

- Yandex API keys go in `example/src/config.ts` (gitignored; copy from `config.example.ts`). Never commit a real key
- Never commit secrets to the repo; npm publish and GitHub release use CI secrets only
- Don't run `release-it` locally against a non-main branch — `.release-it.json` requires `main` and auto-publishes to npm and GitHub Releases