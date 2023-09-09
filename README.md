# Android Pinball With Pics As Prize
Simple pinball for android that rewards users with pictures after a win.

Work based on https://github.com/dozingcat/Vector-Pinball

## How to run

1. Import into android studio
2. Add `prize-images-10k` and `prize-images-100k` and `prize-images-500k` directory in `app/src/main/assets` and some prize images inside
3. Create `local.properties` file in root directory and add properties for AdMob:

```properties
manifest.ad.id=ca-app-pub-XXXXXXXXXXXXXXXXXXXXX
adhelper.ad.id=ca-app-pub-XXXXXXXXXXXXXXXXXXXXX
```