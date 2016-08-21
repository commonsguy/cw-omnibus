Welcome to the source code for [_The Busy Coder's Guide to Android Development_](https://commonsware.com/Android)!

## About the Book

_The Busy Coder's Guide to Android Development_ is a book covering Android application development, from basics
through advanced capabilities. It is updated several times a year and is available through
[the Warescription](https://commonsware.com/warescription) program. Subscribers also have access to office
hours chats and other benefits.

This repository contains the source code for the hundreds of sample apps profiled in the book. These 
samples are updated as the book is, with `git` tags applied to tie sample code versions to book
versions.

The book, and the samples, were written by Mark Murphy. You may also have run into him through
Stack Overflow:

<a href="http://stackoverflow.com/users/115145/commonsware">
<img src="http://stackoverflow.com/users/flair/115145.png" width="208" height="58" alt="profile for CommonsWare at Stack Overflow, Q&amp;A for professional and enthusiast programmers" title="profile for CommonsWare at Stack Overflow, Q&amp;A for professional and enthusiast programmers">
</a>

## About the Code

All of the source code in this archive is licensed under the
Apache 2.0 license except as noted.

The names of the top-level directories roughly correspond to a
shortened form of the chapter titles. Since chapter numbers
change with every release, and since some samples are used by
multiple chapters, I am loathe to put chapter numbers in the
actual directory names.

## Using in Android Studio

All of the projects should have a `build.gradle` file suitable for
importing the project into Android Studio. Note, though, that you
may need to adjust the `compileSdkVersion` in `build.gradle` if it
requests an SDK that you have not downloaded and do not wish to
download. Similarly, you may need to adjust the `buildToolsVersion`
value to refer to a version of the build tools that you have downloaded
from the SDK Manager.

The samples also have stub Gradle wrapper files, enough to allow for
easy import into Android Studio. However,
**always check the `gradle-wrapper.properties` file before importing anything into Android Studio**,
as there is always the chance that somebody has published material linking you to a hacked Gradle installation.

## Using with Command-Line Gradle

Right now, you will need your own local installation of Gradle 2.1
in order to build the projects from the command line, as the repository
does not contain `gradlew` or its corresponding JAR for security reasons.

## List of Samples

- `ACRA/`
  - [`Simple`](https://github.com/commonsguy/cw-omnibus/tree/master/ACRA/Simple)
- `Accessibility/`
  - [`FontScale`](https://github.com/commonsguy/cw-omnibus/tree/master/Accessibility/FontScale)
- `ActionBar/`
  - [`ActionBarDemoNative`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/ActionBarDemoNative)
  - [`HoloColor`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/HoloColor)
  - [`MaterialColor`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/MaterialColor)
  - [`MaterialLogo`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/MaterialLogo)
  - [`OverlayNative`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/OverlayNative)
  - [`SearchView`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/SearchView)
  - [`ShareNative`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/ShareNative)
  - [`VersionedColor`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionBar/VersionedColor)
- `ActionMode/`
  - [`ActionModeMC`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/ActionModeMC)
  - [`LongPress`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/LongPress)
  - [`ManualNative`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/ManualNative)
- `Activities/`
  - [`Explicit`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Explicit)
  - [`Extras`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Extras)
  - [`FullScreen`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/FullScreen)
  - [`LaunchWeb`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/LaunchWeb)
  - [`Lifecycle`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Lifecycle)
- `AlarmManager/`
  - [`AlarmClock`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/AlarmClock)
  - [`AntiDoze`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/AntiDoze)
  - [`Scheduled`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Scheduled)
  - [`Simple`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Simple)
  - [`WakeCast`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/WakeCast)
  - [`Wakeful`](https://github.com/commonsguy/cw-omnibus/tree/master/ActionMode/Wakeful)
- `Animation/`
  - [`AnimatorFade`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/AnimatorFade)
  - [`AnimatorFadeBC`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/AnimatorFadeBC)
  - [`ObjectAnimator`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/ObjectAnimator)
  - [`SlidingPanel`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/SlidingPanel)
  - [`SlidingPanelEx`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/SlidingPanelEx)
  - [`ThreePane`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/ThreePane)
  - [`ThreePaneBC`](https://github.com/commonsguy/cw-omnibus/tree/master/Animation/ThreePaneBC)
- `AppCompat/`
  - [`ActionBar`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/ActionBar)
  - [`ActionBarColor`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/ActionBarColor)
  - [`ActionModeMC`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/ActionModeMC)
  - [`Basic`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/Basic)
  - [`Overlay`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/Overlay)
  - [`SearchView`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/SearchView)
  - [`Share`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/Share)
  - [`StatusBarColor`](https://github.com/commonsguy/cw-omnibus/tree/master/AppCompat/StatusBarColor)
- `AppWidget/`
  - [`LoremWidget`](https://github.com/commonsguy/cw-omnibus/tree/master/AppWidget/LoremWidget)
  - [`PairOfDice`](https://github.com/commonsguy/cw-omnibus/tree/master/AppWidget/PairOfDice)
  - [`Resize`](https://github.com/commonsguy/cw-omnibus/tree/master/AppWidget/Resize)
  - [`TwoOrThreeDice`](https://github.com/commonsguy/cw-omnibus/tree/master/AppWidget/TwoOrThreeDice)
- `Assist/`
  - [`AssistLogger`](https://github.com/commonsguy/cw-omnibus/tree/master/Assist/AssistLogger)
  - [`MoAssist`](https://github.com/commonsguy/cw-omnibus/tree/master/Assist/MoAssist)
  - [`NoAssist`](https://github.com/commonsguy/cw-omnibus/tree/master/Assist/NoAssist)
  - [`TapOffNow`](https://github.com/commonsguy/cw-omnibus/tree/master/Assist/TapOffNow)
- `Backup/`
  - [`Backup`](https://github.com/commonsguy/cw-omnibus/tree/master/Backup/BackupClient)
- `Bandwidth/`
  - [`TrafficMonitor`](https://github.com/commonsguy/cw-omnibus/tree/master/Bandwidth/TrafficMonitor)
- `Basic/`
  - [`Button`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/Button)
  - [`CheckBox`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/CheckBox)
  - [`Field`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/Field)
  - [`ImageView`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/ImageView)
  - [`Label`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/Label)
  - [`RadioButton`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/RadioButton)
  - [`Switch`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/Switch)
  - [`ToggleButton`](https://github.com/commonsguy/cw-omnibus/tree/master/Basic/ToggleButton)
- `BasicMaterial/`
  - [`Button`](https://github.com/commonsguy/cw-omnibus/tree/master/BasicMaterial/Button)
  - [`CheckBox`](https://github.com/commonsguy/cw-omnibus/tree/master/BasicMaterial/CheckBox)
  - [`Field`](https://github.com/commonsguy/cw-omnibus/tree/master/BasicMaterial/Field)
  - [`RadioButton`](https://github.com/commonsguy/cw-omnibus/tree/master/BasicMaterial/RadioButton)
  - [`Switch`](https://github.com/commonsguy/cw-omnibus/tree/master/BasicMaterial/Switch)
- `Binding/`
  - [`Callback/Client`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/Callback/Client)
  - [`Callback/Service`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/Callback/Service)
  - [`Local`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/Local)
  - [`Remote/Client`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/Remote/Client)
  - [`Remote/Service`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/Remote/Service)
  - [`SigCheck/Client`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/SigCheck/Client)
  - [`SigCheck/Service`](https://github.com/commonsguy/cw-omnibus/tree/master/Binding/SigCheck/Service)
- `Bitmaps/`
  - [`InSampleSize`](https://github.com/commonsguy/cw-omnibus/tree/master/Bitmaps/InSampleSize)
- `Calendar/`
  - [`Query`](https://github.com/commonsguy/cw-omnibus/tree/master/Calendar/Query)
- `Camera/`
  - [`Content`](https://github.com/commonsguy/cw-omnibus/tree/master/Camera/Content)
  - [`EXIFRotater`](https://github.com/commonsguy/cw-omnibus/tree/master/Camera/EXIFRotater)
  - [`FileProvider`](https://github.com/commonsguy/cw-omnibus/tree/master/Camera/FileProvider)
  - [`ZXing`](https://github.com/commonsguy/cw-omnibus/tree/master/Camera/ZXing)
- `ConfigChange/`
  - [`Bundle`](https://github.com/commonsguy/cw-omnibus/tree/master/ConfigChange/Bundle)
  - [`DIY`](https://github.com/commonsguy/cw-omnibus/tree/master/ConfigChange/DIY)
  - [`FragmentBundle`](https://github.com/commonsguy/cw-omnibus/tree/master/ConfigChange/FragmentBundle)
  - [`Fragments`](https://github.com/commonsguy/cw-omnibus/tree/master/ConfigChange/Fragments)
- `Contacts/`
  - [`CallLog`](https://github.com/commonsguy/cw-omnibus/tree/master/Contacts/CallLog)
  - [`Inserter`](https://github.com/commonsguy/cw-omnibus/tree/master/Contacts/Inserter)
  - [`Spinners`](https://github.com/commonsguy/cw-omnibus/tree/master/Contacts/Spinners)
- `Containers/`
  - [`LinearPercent`](https://github.com/commonsguy/cw-omnibus/tree/master/Containers/LinearPercent)
  - [`Relative`](https://github.com/commonsguy/cw-omnibus/tree/master/Containers/Relative)
  - [`RelativeOverlap`](https://github.com/commonsguy/cw-omnibus/tree/master/Containers/RelativeOverlap)
  - [`Scroll`](https://github.com/commonsguy/cw-omnibus/tree/master/Containers/Scroll)
  - [`Table`](https://github.com/commonsguy/cw-omnibus/tree/master/Containers/Table)
- `ContentProvider/`
  - [`ConstantsPlus`](https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/ConstantsPlus)
  - [`Files`](https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/Files)
  - [`GrantUriPermissions`](https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/GrantUriPermissions)
  - [`Pipe`](https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/Pipe)
  - [`V4FileProvider`](https://github.com/commonsguy/cw-omnibus/tree/master/ContentProvider/V4FileProvider)
- `CPU-Java/`
  - [`AIDLOverhead`](https://github.com/commonsguy/cw-omnibus/tree/master/CPU-Java/AIDLOverhead)
  - [`GoAsync`](https://github.com/commonsguy/cw-omnibus/tree/master/CPU-Java/GoAsync)
  - [`PrefsPersist`](https://github.com/commonsguy/cw-omnibus/tree/master/CPU-Java/PrefsPersist)
- `Database/`
  - [`ConstantsAssets-AndroidStudio`](https://github.com/commonsguy/cw-omnibus/tree/master/Database/ConstantsAssets-AndroidStudio)
  - [`ConstantsROWID`](https://github.com/commonsguy/cw-omnibus/tree/master/Database/ConstantsROWID)
  - [`ConstantsSecure-AndroidStudio`](https://github.com/commonsguy/cw-omnibus/tree/master/Database/ConstantsSecure-AndroidStudio)
  - [`FTS`](https://github.com/commonsguy/cw-omnibus/tree/master/Database/FTS)
  - [`SQLCipherPassphrase-AndroidStudio`](https://github.com/commonsguy/cw-omnibus/tree/master/Database/SQLCipherPassphrase-AndroidStudio)
- `DataBinding/`
  - [`Basic`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Basic)
  - [`Chained`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Chained)
  - [`Conversion`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Conversion)
  - [`Observable`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Observable)
  - [`Picasso`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Picasso)
  - [`RecyclerView`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/RecyclerView)
  - [`Scored`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Scored)
  - [`Static`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/Static)
  - [`TwoWay`](https://github.com/commonsguy/cw-omnibus/tree/master/DataBinding/TwoWay)
- `DesignSupport/`
  - [`CoordinatedFAB`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/CoordinatedFAB)
  - [`FAB`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/FAB)
  - [`FABClans`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/FABClans)
  - [`FloatingLabel`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/FloatingLabel)
  - [`FloatingLabelNative`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/FloatingLabelNative)
  - [`Snackbar`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/Snackbar)
  - [`SnackbarAction`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/SnackbarAction)
  - [`TabLayout`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/TabLayout)
  - [`TabLayoutPizza`](https://github.com/commonsguy/cw-omnibus/tree/master/DesignSupport/TabLayoutPizza)
- `DeviceAdmin/`
  - [`LockMeNow`](https://github.com/commonsguy/cw-omnibus/tree/master/DeviceAdmin/LockMeNow)
  - [`PasswordEnforcer`](https://github.com/commonsguy/cw-omnibus/tree/master/DeviceAdmin/PasswordEnforcer)
- `Diagnostics/`
  - [`Activity`](https://github.com/commonsguy/cw-omnibus/tree/master/Diagnostics/Activity)
  - [`Overlay`](https://github.com/commonsguy/cw-omnibus/tree/master/Diagnostics/Overlay)
  - [`WebServer`](https://github.com/commonsguy/cw-omnibus/tree/master/Diagnostics/WebServer)
- `Dialogs/`
  - [`Chrono`](https://github.com/commonsguy/cw-omnibus/tree/master/Dialogs/Chrono)
  - [`DialogFragment`](https://github.com/commonsguy/cw-omnibus/tree/master/Dialogs/DialogFragment)
- `Documents/`
  - [`Consumer`](https://github.com/commonsguy/cw-omnibus/tree/master/Documents/Consumer)
  - [`DocumentTree`](https://github.com/commonsguy/cw-omnibus/tree/master/Documents/DocumentTree)
  - [`Provider`](https://github.com/commonsguy/cw-omnibus/tree/master/Documents/Provider)
  - [`TinyTextEditor`](https://github.com/commonsguy/cw-omnibus/tree/master/Documents/TinyTextEditor)
- `DragDrop/`
  - [`Action`](https://github.com/commonsguy/cw-omnibus/tree/master/DragDrop/Action)
  - [`Permissions`](https://github.com/commonsguy/cw-omnibus/tree/master/DragDrop/Permissions)
  - [`Simple`](https://github.com/commonsguy/cw-omnibus/tree/master/DragDrop/Simple)
- `Drawable/`
  - [`AnyVersusNo`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/AnyVersusNo)
  - [`Gradient`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/Gradient)
  - [`NinePatch`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/NinePatch)
  - [`ScaleClip`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/ScaleClip)
  - [`Shape`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/Shape)
  - [`TileMode`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/TileMode)
  - [`Vector`](https://github.com/commonsguy/cw-omnibus/tree/master/Drawable/Vector)
- `EventBus/`
  - [`AsyncDemo`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/AsyncDemo)
  - [`AsyncDemo3`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/AsyncDemo3)
  - [`GreenRobot`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/GreenRobot)
  - [`GreenRobot3`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/GreenRobot3)
  - [`LocalBroadcastManager`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/LocalBroadcastManager)
  - [`Otto`](https://github.com/commonsguy/cw-omnibus/tree/master/EventBus/Otto)
- `Files/`
  - [`FilesEditor`](https://github.com/commonsguy/cw-omnibus/tree/master/Files/FilesEditor)
- `Focus/`
  - [`FocusSampler`](https://github.com/commonsguy/cw-omnibus/tree/master/Focus/FocusSampler)
- `Fonts/`
  - [`FontSampler`](https://github.com/commonsguy/cw-omnibus/tree/master/Fonts/FontSampler)
- `Fragments/`
  - [`ActionBarNative`](https://github.com/commonsguy/cw-omnibus/tree/master/Fragments/ActionBarNative)
  - [`Dynamic`](https://github.com/commonsguy/cw-omnibus/tree/master/Fragments/Dynamic)
  - [`Static`](https://github.com/commonsguy/cw-omnibus/tree/master/Fragments/Static)
- `GridLayout/`
  - [`Sampler`](https://github.com/commonsguy/cw-omnibus/tree/master/GridLayout/Sampler)
- `HTTP/`
  - [`OkHttpProgress`](https://github.com/commonsguy/cw-omnibus/tree/master/HTTP/OkHttpProgress)
  - [`Picasso`](https://github.com/commonsguy/cw-omnibus/tree/master/HTTP/Picasso)
  - [`Retrofit`](https://github.com/commonsguy/cw-omnibus/tree/master/HTTP/Retrofit)
  - [`Volley`](https://github.com/commonsguy/cw-omnibus/tree/master/HTTP/Volley)
- `InputMethod/`
  - [`IMEDemo1`](https://github.com/commonsguy/cw-omnibus/tree/master/InputMethod/IMEDemo1)
  - [`IMEDemo2`](https://github.com/commonsguy/cw-omnibus/tree/master/InputMethod/IMEDemo2)
- `Intents/`
  - [`FauxSender`](https://github.com/commonsguy/cw-omnibus/tree/master/Intents/FauxSender)
  - [`FauxSenderMNC`](https://github.com/commonsguy/cw-omnibus/tree/master/Intents/FauxSenderMNC)
  - [`Local`](https://github.com/commonsguy/cw-omnibus/tree/master/Intents/Local)
  - [`OnBattery`](https://github.com/commonsguy/cw-omnibus/tree/master/Intents/OnBattery)
  - [`OnBoot`](https://github.com/commonsguy/cw-omnibus/tree/master/Intents/OnBoot)
- `Internet/`
  - [`CA`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/CA)
  - [`Download`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/Download)
  - [`HttpClient`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/HttpClient)
  - [`HTTPStacks`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/HTTPStacks)
  - [`HURL`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/HURL)
  - [`OkHttp`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/OkHttp)
  - [`OkHttp3`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/OkHttp3)
  - [`Weather`](https://github.com/commonsguy/cw-omnibus/tree/master/Internet/Weather)
- `Introspection/`
  - [`CPProxy`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/CPProxy)
  - [`EnvDump`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/EnvDump)
  - [`FauxSender`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/FauxSender)
  - [`Launchalot`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/Launchalot)
  - [`PrefActivities`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/PrefActivities)
  - [`ProcessText`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/ProcessText)
  - [`ProcessTextBlocker`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/ProcessTextBlocker)
  - [`QuickSender`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/QuickSender)
  - [`Resolver`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/Resolver)
  - [`SAWMonitor`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/SAWMonitor)
  - [`SAWMonitorTile`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/SAWMonitorTile)
  - [`URLHandler`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/URLHandler)
  - [`URLHandlerMNC`](https://github.com/commonsguy/cw-omnibus/tree/master/Introspection/URLHandlerMNC)
- `Jank/`
  - [`FrameMetrics`](https://github.com/commonsguy/cw-omnibus/tree/master/Jank/FrameMetrics)
  - [`ThreePaneBC`](https://github.com/commonsguy/cw-omnibus/tree/master/Jank/ThreePaneBC)
- `JNI/`
  - [`WeakBench`](https://github.com/commonsguy/cw-omnibus/tree/master/JNI/WeakBench)
- `JobScheduler/`
  - [`Content`](https://github.com/commonsguy/cw-omnibus/tree/master/JobScheduler/Content)
  - [`PowerHungry`](https://github.com/commonsguy/cw-omnibus/tree/master/JobScheduler/PowerHungry)
- `JVM/`
  - [`InterpreterService`](https://github.com/commonsguy/cw-omnibus/tree/master/JVM/InterpreterService)
- `KBMouse/`
  - [`Context`](https://github.com/commonsguy/cw-omnibus/tree/master/KBMouse/Context)
  - [`CopyPaste`](https://github.com/commonsguy/cw-omnibus/tree/master/KBMouse/CopyPaste)
  - [`Hotkeys`](https://github.com/commonsguy/cw-omnibus/tree/master/KBMouse/Hotkeys)
  - [`HotkeysN`](https://github.com/commonsguy/cw-omnibus/tree/master/KBMouse/HotkeysN)
  - [`Tooltip`](https://github.com/commonsguy/cw-omnibus/tree/master/KBMouse/Tooltip)
- `LargeScreen/`
  - [`EU4You`](https://github.com/commonsguy/cw-omnibus/tree/master/LargeScreen/EU4You)
  - [`EU4YouSingleActivity`](https://github.com/commonsguy/cw-omnibus/tree/master/LargeScreen/EU4YouSingleActivity)
  - [`EU4YouSlidingPane`](https://github.com/commonsguy/cw-omnibus/tree/master/LargeScreen/EU4YouSlidingPane)
  - [`EU4YouStaticCountries`](https://github.com/commonsguy/cw-omnibus/tree/master/LargeScreen/EU4YouStaticCountries)
- `Leaks/`
  - [`AsyncTask`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/AsyncTask)
  - [`ConfigChange`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/ConfigChange)
  - [`Downloader`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/Downloader)
  - [`LeakedThread`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/LeakedThread)
  - [`StaticWidget`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/StaticWidget)
  - [`StaticWidgetLC`](https://github.com/commonsguy/cw-omnibus/tree/master/Leaks/StaticWidgetLC)
- `Leanback/`
  - [`VideoBrowse`](https://github.com/commonsguy/cw-omnibus/tree/master/Leanback/VideoBrowse)
