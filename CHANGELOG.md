## [1.8.1](https://github.com/Retribution-Mod/retribution-manager/compare/v1.8.0...v1.8.1) (2026-08-21)


### Bug Fixes

* **aikido:** isolate install intent, pin actions, add network security config ([25f584d](https://github.com/Retribution-Mod/retribution-manager/commit/25f584dbedc1e29345bf1082d134e7e1da07ccb0))

# [1.8.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.7.0...v1.8.0) (2026-08-21)


### Features

* deep-link preview + bundle update tile + branded dashboard ([03c23f3](https://github.com/Retribution-Mod/retribution-manager/commit/03c23f3b4df59e1b52f628d2d7bb3ddf42154355))

# [1.7.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.6.0...v1.7.0) (2026-08-21)


### Features

* Retribution-branded home dashboard ([84c1bb9](https://github.com/Retribution-Mod/retribution-manager/commit/84c1bb93c0b7a9b42bdbfc039fb947eadf45e1bb))

# [1.6.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.5.3...v1.6.0) (2026-08-21)


### Bug Fixes

* build errors in manager update prompt ([da3b803](https://github.com/Retribution-Mod/retribution-manager/commit/da3b8035195bd379c91f2cd7a9d6e63cc61c4a6d))
* use explicit xposed release tag for module downloads ([1640c07](https://github.com/Retribution-Mod/retribution-manager/commit/1640c07ea12dff37b308dba1ebceada59b39b767))


### Features

* prompt to re-patch when new Xposed module is available ([6e1b496](https://github.com/Retribution-Mod/retribution-manager/commit/6e1b4969b3f0d5b07643822062a7773c912e9a0d))

## [1.5.3](https://github.com/Retribution-Mod/retribution-manager/compare/v1.5.2...v1.5.3) (2026-08-21)


### Bug Fixes

* **security:** Fixed arbitrary module injection vulnerability by implementing strict URL validation for LSPatch module downloads. ([#1](https://github.com/Retribution-Mod/retribution-manager/issues/1)) ([638d063](https://github.com/Retribution-Mod/retribution-manager/commit/638d0636ddde896afde8cf2f1582a55789aced0a))

## [1.5.2](https://github.com/Retribution-Mod/retribution-manager/compare/v1.5.1...v1.5.2) (2026-08-20)


### Bug Fixes

* **security:** Added SHA-256 hash verification for downloaded APK files against tracker API hashes to prevent trojanized package installation. ([020a86a](https://github.com/Retribution-Mod/retribution-manager/commit/020a86ab3eaa9e2284c01a02b9ec63292f93ecbb))

## [1.5.1](https://github.com/Retribution-Mod/retribution-manager/compare/v1.5.0...v1.5.1) (2026-08-20)


### Bug Fixes

* show and select the latest compatible Discord versions ([38af9e7](https://github.com/Retribution-Mod/retribution-manager/commit/38af9e7d7b77ab36a174f9cb88e625c578d046ae))

# [1.5.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.4.0...v1.5.0) (2026-08-20)


### Bug Fixes

* replace deprecated Project.exec in build script with providers.exec for Gradle 9 compatibility ([ba1c1df](https://github.com/Retribution-Mod/retribution-manager/commit/ba1c1df6b7b7c30191d03adf67953631e1d1fd8f))


### Features

* add plugin:// theme:// font:// and manager://bundle deep link intent filters and routing ([7977226](https://github.com/Retribution-Mod/retribution-manager/commit/79772260442a1f8bdea35b5f35f9db1af2fae4d6))

# [1.4.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.3.0...v1.4.0) (2026-08-20)


### Features

* install chooser for Old/New/Next variants with separate Next package ([60e406b](https://github.com/Retribution-Mod/retribution-manager/commit/60e406b82b2a2a1b70cfb633de32c7920551755e))

# [1.3.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.2.1...v1.3.0) (2026-08-19)


### Features

* replace app icons with new manager (fist/sword) and bundle (eye) logos ([9c025fe](https://github.com/Retribution-Mod/retribution-manager/commit/9c025fe633744a4ff0e61199d1b7de42cc30ae0c))

## [1.2.1](https://github.com/Retribution-Mod/retribution-manager/compare/v1.2.0...v1.2.1) (2026-08-19)


### Bug Fixes

* regenerate transparent icon mask, add in-app icon asset, overwrite APK foregrounds ([b46cad2](https://github.com/Retribution-Mod/retribution-manager/commit/b46cad274c93992c7d95fa81a63ff0a1098e5245))

# [1.2.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.1.1...v1.2.0) (2026-08-19)


### Features

* implement two-layer adaptive icon pipeline to fix blurry patched icons ([bebb270](https://github.com/Retribution-Mod/retribution-manager/commit/bebb2700d701b6007a2aca0816d7325da8f6b29a))

## [1.1.1](https://github.com/Retribution-Mod/retribution-manager/compare/v1.1.0...v1.1.1) (2026-08-19)


### Bug Fixes

* **manager:** show bundle icon in manager UI and fix adaptive icon patching ([69dfe2d](https://github.com/Retribution-Mod/retribution-manager/commit/69dfe2dd0ae55aaec6a3de0bc47a57e399e94104))

# [1.1.0](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.6...v1.1.0) (2026-08-19)


### Features

* update app and bundle icon to the new Retribution angry icon ([72bf7e8](https://github.com/Retribution-Mod/retribution-manager/commit/72bf7e8551ddbed204c929f7e16efd3bd48e16f6))
* use user-provided bundle and manager icons ([4f7df0b](https://github.com/Retribution-Mod/retribution-manager/commit/4f7df0be5313ab5c153a60a1c3b4832911887302))

## [1.0.6](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.5...v1.0.6) (2026-08-19)


### Bug Fixes

* **manager:** remove unused high-risk permissions to reduce Play Protect warning ([2caa2f1](https://github.com/Retribution-Mod/retribution-manager/commit/2caa2f1a3fc333377d227c5eb607678e56d18a48))

## [1.0.5](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.4...v1.0.5) (2026-08-19)


### Bug Fixes

* **manager:** correct adaptive icon resource IDs and bump binary-resources to 2.1.0 ([e983e11](https://github.com/Retribution-Mod/retribution-manager/commit/e983e11c31467674223fa8eb7aeb3f5dde0b41a8))
* **manager:** keep binary-resources 2.0.0; 2.1.0 is not in snapshots repo ([ab98bab](https://github.com/Retribution-Mod/retribution-manager/commit/ab98bab04884a503139f258e8058812e0978db99))
* **manager:** read remote icon bytes instead of returning InputStream ([05c7a4a](https://github.com/Retribution-Mod/retribution-manager/commit/05c7a4a8949947bc5eef3a88fa40a0e7e2dbfe12))
* **manager:** safer icon loading with full error logging ([7801aba](https://github.com/Retribution-Mod/retribution-manager/commit/7801abaa2ec54120b1b585f0b74f810ba4d9114a))

## [1.0.4](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.3...v1.0.4) (2026-08-19)


### Bug Fixes

* **manager:** bundle retribution icon as asset, load it instead of network ([0407452](https://github.com/Retribution-Mod/retribution-manager/commit/04074525c190942313d800711666dea69dd3438f))

## [1.0.3](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.2...v1.0.3) (2026-08-19)


### Bug Fixes

* **manager:** scale modded app icon into adaptive safe-zone canvas ([a0419ab](https://github.com/Retribution-Mod/retribution-manager/commit/a0419ab06ffa5777c9a622c57e47815c32f63f74))
* **manager:** set User-Agent and longer read timeout for icon download ([718e1b3](https://github.com/Retribution-Mod/retribution-manager/commit/718e1b3559d1ff780c23c5c1a579fe5ac7afa10c))

## [1.0.2](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.1...v1.0.2) (2026-08-19)


### Bug Fixes

* **manager:** correct arsc resource IDs and scale icon for crisp launcher icon ([bc44335](https://github.com/Retribution-Mod/retribution-manager/commit/bc44335cada833f3722bcbcfcb66403968a9dbac))
* **manager:** setup keystore before build so release APK is signed ([71af1e3](https://github.com/Retribution-Mod/retribution-manager/commit/71af1e3db326aea815b460b20f38c4a3f8481d27))

## [1.0.2](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.1...v1.0.2) (2026-08-19)


### Bug Fixes

* **manager:** correct arsc resource IDs and scale icon for crisp launcher icon ([bc44335](https://github.com/Retribution-Mod/retribution-manager/commit/bc44335cada833f3722bcbcfcb66403968a9dbac))

## [1.0.2](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.1...v1.0.2) (2026-08-18)


### Bug Fixes

* **manager:** correct arsc resource IDs and scale icon for crisp launcher icon ([bc44335](https://github.com/Retribution-Mod/retribution-manager/commit/bc44335cada833f3722bcbcfcb66403968a9dbac))

## [1.0.1](https://github.com/Retribution-Mod/retribution-manager/compare/v1.0.0...v1.0.1) (2026-08-18)


### Bug Fixes

* **manager:** only delete drawable icon if it already exists ([e6f2b1e](https://github.com/Retribution-Mod/retribution-manager/commit/e6f2b1e53962f0319b7ebb7da2a465f4eb7b0e42))

# 1.0.0 (2026-08-18)


### Features

* **manager:** install Retribution icon from public URL ([9451c02](https://github.com/Retribution-Mod/retribution-manager/commit/9451c02d9eb781174b18f1dcdc08cd27c10bd6e2))

# 1.0.0 (2026-08-18)


### Features

* **manager:** install Retribution icon from public URL ([9451c02](https://github.com/Retribution-Mod/retribution-manager/commit/9451c02d9eb781174b18f1dcdc08cd27c10bd6e2))

# [1.3.0](https://github.com/revenge-mod/revenge-manager/compare/v1.2.0...v1.3.0) (2026-01-12)


### Bug Fixes

* **download:** Handle app update download failures ([03b614d](https://github.com/revenge-mod/revenge-manager/commit/03b614da5b05daecb0ff0ff39141931523f40f06))
* **DownloadManager:** Don't cache incomplete downloads ([0b3890b](https://github.com/revenge-mod/revenge-manager/commit/0b3890b691d8d9e82e068ca4452526d0e4159a3a))
* **DownloadManager:** Replace system DownloadManager with Ktor ([0ffe251](https://github.com/revenge-mod/revenge-manager/commit/0ffe2516fe175a2028e5fc302f4c5eb04f82d722))
* **DownloadManager:** Replace system DownloadManager with Ktor ([#48](https://github.com/revenge-mod/revenge-manager/issues/48)) ([bd1faca](https://github.com/revenge-mod/revenge-manager/commit/bd1faca774215f23af044e0e492f4986803effe1))
* **InstallStep/Shizuku:** Installation method fallback ([#37](https://github.com/revenge-mod/revenge-manager/issues/37)) ([422ae0a](https://github.com/revenge-mod/revenge-manager/commit/422ae0aeb6814f8db67e77480b30de7fb54cd4bb))
* update strings casing ([#50](https://github.com/revenge-mod/revenge-manager/issues/50)) ([2a3da1b](https://github.com/revenge-mod/revenge-manager/commit/2a3da1bf6801d1d1a14376c5915d0f421b19f3ca))


### Features

* Add battery optimization request dialog ([9897c4e](https://github.com/revenge-mod/revenge-manager/commit/9897c4e970c4be6f3a7a7804dfece30d01abdcaa))

# [1.3.0-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.2.1-dev.1...v1.3.0-dev.1) (2026-01-12)


### Bug Fixes

* **download:** Handle app update download failures ([03b614d](https://github.com/revenge-mod/revenge-manager/commit/03b614da5b05daecb0ff0ff39141931523f40f06))
* **DownloadManager:** Don't cache incomplete downloads ([0b3890b](https://github.com/revenge-mod/revenge-manager/commit/0b3890b691d8d9e82e068ca4452526d0e4159a3a))
* **DownloadManager:** Replace system DownloadManager with Ktor ([0ffe251](https://github.com/revenge-mod/revenge-manager/commit/0ffe2516fe175a2028e5fc302f4c5eb04f82d722))
* **DownloadManager:** Replace system DownloadManager with Ktor ([#48](https://github.com/revenge-mod/revenge-manager/issues/48)) ([bd1faca](https://github.com/revenge-mod/revenge-manager/commit/bd1faca774215f23af044e0e492f4986803effe1))


### Features

* Add battery optimization request dialog ([9897c4e](https://github.com/revenge-mod/revenge-manager/commit/9897c4e970c4be6f3a7a7804dfece30d01abdcaa))

## [1.2.1-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.2.0...v1.2.1-dev.1) (2025-10-07)


### Bug Fixes

* **InstallStep/Shizuku:** Installation method fallback ([#37](https://github.com/revenge-mod/revenge-manager/issues/37)) ([422ae0a](https://github.com/revenge-mod/revenge-manager/commit/422ae0aeb6814f8db67e77480b30de7fb54cd4bb))

# [1.2.0](https://github.com/revenge-mod/revenge-manager/compare/v1.1.0...v1.2.0) (2025-03-17)


### Bug Fixes

* `fillVoid` when replacing AndroidManifest.xml in base apk ([c623d1d](https://github.com/revenge-mod/revenge-manager/commit/c623d1d174b922e27c1b9d90d95ceff95a48bb3f))
* App version comparison for updates ([b2a5122](https://github.com/revenge-mod/revenge-manager/commit/b2a51228b41d349e5b973557232624d2e1547895))
* Correct version code parsing and update dialog comparison ([e6be2f5](https://github.com/revenge-mod/revenge-manager/commit/e6be2f501820ae9cc3663a4cfaf4ac7e125dcc01))
* Don't cache canceled partial downloads ([b0a637d](https://github.com/revenge-mod/revenge-manager/commit/b0a637d7f6987fa4549ebcd2df67b603ce0b2641)), closes [#21](https://github.com/revenge-mod/revenge-manager/issues/21)
* Don't remove "unnecessary" prefix handling ([7266bf5](https://github.com/revenge-mod/revenge-manager/commit/7266bf5b7e5c7d642a7d79fc1f8ebc111b34d8cc))
* Invalid updater URLs ([4d01c52](https://github.com/revenge-mod/revenge-manager/commit/4d01c52a21922dc0897cb76917139a9c1bc59e2f))
* Parse update download URL from API response ([d9b6916](https://github.com/revenge-mod/revenge-manager/commit/d9b6916a52f72a3bd27fcfb0ca5787fdc084fdc3))
* Remove unnecessary prefix handling ([f226621](https://github.com/revenge-mod/revenge-manager/commit/f22662196d3be971d1c46853fdcb165d2158213a))


### Features

* Add progress tracking to update download ([75602c1](https://github.com/revenge-mod/revenge-manager/commit/75602c1b4a7e2dc41d6a944f9aab0a352d318baa))
* Update mirror list, and fallback to default on invalid selection ([bb31a90](https://github.com/revenge-mod/revenge-manager/commit/bb31a90c714eff84e163ca336d9ee23eea80252c))

# [1.2.0-dev.4](https://github.com/revenge-mod/revenge-manager/compare/v1.2.0-dev.3...v1.2.0-dev.4) (2025-03-17)


### Bug Fixes

* Don't cache canceled partial downloads ([b0a637d](https://github.com/revenge-mod/revenge-manager/commit/b0a637d7f6987fa4549ebcd2df67b603ce0b2641)), closes [#21](https://github.com/revenge-mod/revenge-manager/issues/21)

# [1.2.0-dev.3](https://github.com/revenge-mod/revenge-manager/compare/v1.2.0-dev.2...v1.2.0-dev.3) (2025-03-17)


### Bug Fixes

* `fillVoid` when replacing AndroidManifest.xml in base apk ([c623d1d](https://github.com/revenge-mod/revenge-manager/commit/c623d1d174b922e27c1b9d90d95ceff95a48bb3f))

# [1.2.0-dev.2](https://github.com/revenge-mod/revenge-manager/compare/v1.2.0-dev.1...v1.2.0-dev.2) (2025-03-04)


### Features

* Update mirror list, and fallback to default on invalid selection ([bb31a90](https://github.com/revenge-mod/revenge-manager/commit/bb31a90c714eff84e163ca336d9ee23eea80252c))

# [1.2.0-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.1.1-dev.4...v1.2.0-dev.1) (2025-02-09)


### Bug Fixes

* App version comparison for updates ([b2a5122](https://github.com/revenge-mod/revenge-manager/commit/b2a51228b41d349e5b973557232624d2e1547895))
* Parse update download URL from API response ([d9b6916](https://github.com/revenge-mod/revenge-manager/commit/d9b6916a52f72a3bd27fcfb0ca5787fdc084fdc3))


### Features

* Add progress tracking to update download ([75602c1](https://github.com/revenge-mod/revenge-manager/commit/75602c1b4a7e2dc41d6a944f9aab0a352d318baa))

## [1.1.1-dev.4](https://github.com/revenge-mod/revenge-manager/compare/v1.1.1-dev.3...v1.1.1-dev.4) (2025-02-05)


### Bug Fixes

* Don't remove "unnecessary" prefix handling ([7266bf5](https://github.com/revenge-mod/revenge-manager/commit/7266bf5b7e5c7d642a7d79fc1f8ebc111b34d8cc))

## [1.1.1-dev.3](https://github.com/revenge-mod/revenge-manager/compare/v1.1.1-dev.2...v1.1.1-dev.3) (2025-01-20)


### Bug Fixes

* Remove unnecessary prefix handling ([f226621](https://github.com/revenge-mod/revenge-manager/commit/f22662196d3be971d1c46853fdcb165d2158213a))

## [1.1.1-dev.2](https://github.com/revenge-mod/revenge-manager/compare/v1.1.1-dev.1...v1.1.1-dev.2) (2025-01-20)


### Bug Fixes

* Correct version code parsing and update dialog comparison ([e6be2f5](https://github.com/revenge-mod/revenge-manager/commit/e6be2f501820ae9cc3663a4cfaf4ac7e125dcc01))

## [1.1.1-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.1.0...v1.1.1-dev.1) (2025-01-20)


### Bug Fixes

* Invalid updater URLs ([4d01c52](https://github.com/revenge-mod/revenge-manager/commit/4d01c52a21922dc0897cb76917139a9c1bc59e2f))

# [1.1.0](https://github.com/revenge-mod/revenge-manager/compare/v1.0.0...v1.1.0) (2025-01-16)


### Bug Fixes

* Display correct group status in between steps ([706a547](https://github.com/revenge-mod/revenge-manager/commit/706a547f65e9e72f13cf56a8e0102db6617b4b9a))
* Switch to JingMatrix/LSPatch to fix patching on Android 15 ([#10](https://github.com/revenge-mod/revenge-manager/issues/10)) ([343a91d](https://github.com/revenge-mod/revenge-manager/commit/343a91d83c3e394c3cd6e396de5d4a33a5ae3dbe))


### Features

* Cleanup about page and remove previous team members ([39ee2c6](https://github.com/revenge-mod/revenge-manager/commit/39ee2c654c62b63045d918cf7ffddcee55f6614f))
* Implement automatic mirror switching and fix handling of stalled downloads ([#14](https://github.com/revenge-mod/revenge-manager/issues/14)) ([9921793](https://github.com/revenge-mod/revenge-manager/commit/992179349641e96fc383650767b196557767ea22))
* Temporarily use DEFAULT if SHIZUKU permissions are not granted ([#15](https://github.com/revenge-mod/revenge-manager/issues/15)) ([f784bf6](https://github.com/revenge-mod/revenge-manager/commit/f784bf622d764c2c9e43d0ab20ef3b7c83de13c1))

# [1.1.0-dev.3](https://github.com/revenge-mod/revenge-manager/compare/v1.1.0-dev.2...v1.1.0-dev.3) (2025-01-16)


### Features

* Cleanup about page and remove previous team members ([39ee2c6](https://github.com/revenge-mod/revenge-manager/commit/39ee2c654c62b63045d918cf7ffddcee55f6614f))

# [1.1.0-dev.2](https://github.com/revenge-mod/revenge-manager/compare/v1.1.0-dev.1...v1.1.0-dev.2) (2025-01-16)


### Bug Fixes

* Display correct group status in between steps ([706a547](https://github.com/revenge-mod/revenge-manager/commit/706a547f65e9e72f13cf56a8e0102db6617b4b9a))


### Features

* Implement automatic mirror switching and fix handling of stalled downloads ([#14](https://github.com/revenge-mod/revenge-manager/issues/14)) ([9921793](https://github.com/revenge-mod/revenge-manager/commit/992179349641e96fc383650767b196557767ea22))

# [1.1.0-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.0.1-dev.1...v1.1.0-dev.1) (2025-01-16)


### Features

* Temporarily use DEFAULT if SHIZUKU permissions are not granted ([#15](https://github.com/revenge-mod/revenge-manager/issues/15)) ([f784bf6](https://github.com/revenge-mod/revenge-manager/commit/f784bf622d764c2c9e43d0ab20ef3b7c83de13c1))

## [1.0.1-dev.1](https://github.com/revenge-mod/revenge-manager/compare/v1.0.0...v1.0.1-dev.1) (2024-11-24)


### Bug Fixes

* Switch to JingMatrix/LSPatch to fix patching on Android 15 ([#10](https://github.com/revenge-mod/revenge-manager/issues/10)) ([343a91d](https://github.com/revenge-mod/revenge-manager/commit/343a91d83c3e394c3cd6e396de5d4a33a5ae3dbe))

# 1.0.0 (2024-10-20)


### Bug Fixes

* actually increment resource index ([980811d](https://github.com/revenge-mod/revenge-manager/commit/980811d4107acf425d689b3f0b831e58d064b1a4))
* Appearance tab causing crashes ([bdff280](https://github.com/revenge-mod/revenge-manager/commit/bdff280e29a6cb316ac8ad936621281c8c1f8c18))
* correct session handling for split package ([8deea0f](https://github.com/revenge-mod/revenge-manager/commit/8deea0fed3fa6b53ac5db236404052367e4dc960))
* downgrade lspatch & add proguard rules ([#69](https://github.com/revenge-mod/revenge-manager/issues/69)) ([4909003](https://github.com/revenge-mod/revenge-manager/commit/4909003439b4cc86b15c338c641528d3cce9a81a))
* Fix download link ([2ab4e50](https://github.com/revenge-mod/revenge-manager/commit/2ab4e501632683ea6995c41fff419180999935c1))
* fix maisy's username ([b35908e](https://github.com/revenge-mod/revenge-manager/commit/b35908eb4441404cf6ccf5c498ddc9de0eb64267))
* hack for red patched icon ([f9ae0ba](https://github.com/revenge-mod/revenge-manager/commit/f9ae0ba7051b53b172a535e938c876286834c6d4))
* Incorrect xposed module cache path ([4c7324b](https://github.com/revenge-mod/revenge-manager/commit/4c7324bc2cdf88a6d829cf7cfe1082829dcc3b11))
* migrate to new update tracker ([#6](https://github.com/revenge-mod/revenge-manager/issues/6)) ([35eafd1](https://github.com/revenge-mod/revenge-manager/commit/35eafd120787139d1673254dc713aa097f8ea566))
* only show launch button if install is successful ([#63](https://github.com/revenge-mod/revenge-manager/issues/63)) ([b302f93](https://github.com/revenge-mod/revenge-manager/commit/b302f93e3ea643f83c5f6426017b1a9976998a67)), closes [#35](https://github.com/revenge-mod/revenge-manager/issues/35)
* **README:** imgshield link ([#2](https://github.com/revenge-mod/revenge-manager/issues/2)) ([6266d65](https://github.com/revenge-mod/revenge-manager/commit/6266d65f1a08cf9201195c0310567a72cd9cf079))
* request Shizuku permissions only when necessary ([#64](https://github.com/revenge-mod/revenge-manager/issues/64)) ([cc449da](https://github.com/revenge-mod/revenge-manager/commit/cc449da39d02eb9cfc91adb6bdd17230fa5390cd))
* **ui:** discord link on about screen ([#4](https://github.com/revenge-mod/revenge-manager/issues/4)) ([8cbe4ca](https://github.com/revenge-mod/revenge-manager/commit/8cbe4ca6e37dbc1faef4d1f4a6b414ebffe2dc42))


### Features

* add installer selection UI ([8e484a8](https://github.com/revenge-mod/revenge-manager/commit/8e484a8e969b3e68da3b033122a36f856d2ac2f9))
* add shizuku dependencies ([42acb6a](https://github.com/revenge-mod/revenge-manager/commit/42acb6a534a39553ea188f70d5b793c207550efb))
* downgrade shizuku and finish installer ([8ede0cd](https://github.com/revenge-mod/revenge-manager/commit/8ede0cdc861fa7883f4f3872168ab45155002b21))
* Download from Revenge ([8ec0ca7](https://github.com/revenge-mod/revenge-manager/commit/8ec0ca729826c720c2733f02b1160465fb2259ae))
* Download updates from Revenge ([9901299](https://github.com/revenge-mod/revenge-manager/commit/9901299a16d5901f4681dc70cd0ee375bfc1874c))
* **DownloadManager:** properly handle downloading ([#66](https://github.com/revenge-mod/revenge-manager/issues/66)) ([d65cd45](https://github.com/revenge-mod/revenge-manager/commit/d65cd45a829c1b136ea704840de70deb5b64419a))
* enable release optimizations ([#67](https://github.com/revenge-mod/revenge-manager/issues/67)) ([ed9fb6e](https://github.com/revenge-mod/revenge-manager/commit/ed9fb6e80535d2c73eea6d112f7366debe595fc3))
* Rebrand to Revenge ([bdc40e9](https://github.com/revenge-mod/revenge-manager/commit/bdc40e9db5de7e5b272727cbad7ff3f3c5b17ac3))
* Rebrand to Revenge ([#7](https://github.com/revenge-mod/revenge-manager/issues/7)) ([eb13258](https://github.com/revenge-mod/revenge-manager/commit/eb1325834acc8129d3b61e4ef9ab4cfd7c665f3f))
* shizuku binding and permission handling ([493ec80](https://github.com/revenge-mod/revenge-manager/commit/493ec80eccff65816c6f7c17d5027a1ff2fd9d7f))
* shizuku installer ([#55](https://github.com/revenge-mod/revenge-manager/issues/55)) ([c60414b](https://github.com/revenge-mod/revenge-manager/commit/c60414bb1e7254a0f64b43d8aedbdeb380d98a73))
* shizuku user service ([3f2b18f](https://github.com/revenge-mod/revenge-manager/commit/3f2b18f52de8c8552408311091845e9248fdf276))
* update about screen links ([59c36c2](https://github.com/revenge-mod/revenge-manager/commit/59c36c288fbcb1144067b150644e7b511180995b))

# [1.0.0-dev.4](https://github.com/revenge-mod/revenge-manager/compare/v1.0.0-dev.3...v1.0.0-dev.4) (2024-10-04)


### Features

* Rebrand to Revenge ([bdc40e9](https://github.com/revenge-mod/revenge-manager/commit/bdc40e9db5de7e5b272727cbad7ff3f3c5b17ac3))

# [1.0.0-dev.3](https://github.com/revenge-mod/revenge-manager/compare/v1.0.0-dev.2...v1.0.0-dev.3) (2024-09-18)


### Bug Fixes

* Appearance tab causing crashes ([bdff280](https://github.com/revenge-mod/revenge-manager/commit/bdff280e29a6cb316ac8ad936621281c8c1f8c18))

# [1.0.0-dev.2](https://github.com/revenge-mod/revenge-manager/compare/v1.0.0-dev.1...v1.0.0-dev.2) (2024-09-16)


### Features

* Rebrand to Revenge ([#7](https://github.com/revenge-mod/revenge-manager/issues/7)) ([eb13258](https://github.com/revenge-mod/revenge-manager/commit/eb1325834acc8129d3b61e4ef9ab4cfd7c665f3f))

# 1.0.0-dev.1 (2024-09-16)


### Bug Fixes

* actually increment resource index ([980811d](https://github.com/revenge-mod/revenge-manager/commit/980811d4107acf425d689b3f0b831e58d064b1a4))
* correct session handling for split package ([8deea0f](https://github.com/revenge-mod/revenge-manager/commit/8deea0fed3fa6b53ac5db236404052367e4dc960))
* downgrade lspatch & add proguard rules ([#69](https://github.com/revenge-mod/revenge-manager/issues/69)) ([4909003](https://github.com/revenge-mod/revenge-manager/commit/4909003439b4cc86b15c338c641528d3cce9a81a))
* Fix download link ([2ab4e50](https://github.com/revenge-mod/revenge-manager/commit/2ab4e501632683ea6995c41fff419180999935c1))
* fix maisy's username ([b35908e](https://github.com/revenge-mod/revenge-manager/commit/b35908eb4441404cf6ccf5c498ddc9de0eb64267))
* hack for red patched icon ([f9ae0ba](https://github.com/revenge-mod/revenge-manager/commit/f9ae0ba7051b53b172a535e938c876286834c6d4))
* Incorrect xposed module cache path ([4c7324b](https://github.com/revenge-mod/revenge-manager/commit/4c7324bc2cdf88a6d829cf7cfe1082829dcc3b11))
* migrate to new update tracker ([#6](https://github.com/revenge-mod/revenge-manager/issues/6)) ([35eafd1](https://github.com/revenge-mod/revenge-manager/commit/35eafd120787139d1673254dc713aa097f8ea566))
* only show launch button if install is successful ([#63](https://github.com/revenge-mod/revenge-manager/issues/63)) ([b302f93](https://github.com/revenge-mod/revenge-manager/commit/b302f93e3ea643f83c5f6426017b1a9976998a67)), closes [#35](https://github.com/revenge-mod/revenge-manager/issues/35)
* **README:** imgshield link ([#2](https://github.com/revenge-mod/revenge-manager/issues/2)) ([6266d65](https://github.com/revenge-mod/revenge-manager/commit/6266d65f1a08cf9201195c0310567a72cd9cf079))
* request Shizuku permissions only when necessary ([#64](https://github.com/revenge-mod/revenge-manager/issues/64)) ([cc449da](https://github.com/revenge-mod/revenge-manager/commit/cc449da39d02eb9cfc91adb6bdd17230fa5390cd))
* **ui:** discord link on about screen ([#4](https://github.com/revenge-mod/revenge-manager/issues/4)) ([8cbe4ca](https://github.com/revenge-mod/revenge-manager/commit/8cbe4ca6e37dbc1faef4d1f4a6b414ebffe2dc42))


### Features

* add installer selection UI ([8e484a8](https://github.com/revenge-mod/revenge-manager/commit/8e484a8e969b3e68da3b033122a36f856d2ac2f9))
* add shizuku dependencies ([42acb6a](https://github.com/revenge-mod/revenge-manager/commit/42acb6a534a39553ea188f70d5b793c207550efb))
* downgrade shizuku and finish installer ([8ede0cd](https://github.com/revenge-mod/revenge-manager/commit/8ede0cdc861fa7883f4f3872168ab45155002b21))
* Download from Revenge ([8ec0ca7](https://github.com/revenge-mod/revenge-manager/commit/8ec0ca729826c720c2733f02b1160465fb2259ae))
* Download updates from Revenge ([9901299](https://github.com/revenge-mod/revenge-manager/commit/9901299a16d5901f4681dc70cd0ee375bfc1874c))
* **DownloadManager:** properly handle downloading ([#66](https://github.com/revenge-mod/revenge-manager/issues/66)) ([d65cd45](https://github.com/revenge-mod/revenge-manager/commit/d65cd45a829c1b136ea704840de70deb5b64419a))
* enable release optimizations ([#67](https://github.com/revenge-mod/revenge-manager/issues/67)) ([ed9fb6e](https://github.com/revenge-mod/revenge-manager/commit/ed9fb6e80535d2c73eea6d112f7366debe595fc3))
* shizuku binding and permission handling ([493ec80](https://github.com/revenge-mod/revenge-manager/commit/493ec80eccff65816c6f7c17d5027a1ff2fd9d7f))
* shizuku installer ([#55](https://github.com/revenge-mod/revenge-manager/issues/55)) ([c60414b](https://github.com/revenge-mod/revenge-manager/commit/c60414bb1e7254a0f64b43d8aedbdeb380d98a73))
* shizuku user service ([3f2b18f](https://github.com/revenge-mod/revenge-manager/commit/3f2b18f52de8c8552408311091845e9248fdf276))
* update about screen links ([59c36c2](https://github.com/revenge-mod/revenge-manager/commit/59c36c288fbcb1144067b150644e7b511180995b))
