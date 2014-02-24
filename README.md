BlueJ.leJOS
===========

**BlueJ.leJOS** is an extension for the popular [BlueJ](http://www.bluej.org)
development environment, integrating and connecting the
[leJOS NXJ Firmware](http://www.lejos.org/) for *Lego Mindstroms NXT*. It allows
to develop Java programs for the NXT brick.

The extension is developed by [Jonas Neugebauer](http://ddi.uni-paderborn.de/personen/jonas-neugebauer.html) and published under the
[GNU General Public License version 2](LICENSE).

Content
=======
1. [Installation/Quick Guide](#quick-guide)
1. [Installation/Step-by-Step Guide](#step-by-step-guide)
1. [How to use/Configuration](#configuration)
1. [How to use/Usage](#usage)
1. [FAQ](#faq)

Installation
============

Quick Guide
-----------
1. Download and install a [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
2. Download and install [BlueJ](http://bluej.org/download/download.html)
3. Download and install [leJOS](http://www.lejos.org/nxj-downloads.php)
4. Download the extensions' [jar-file](jar/bluej-lejos.jar) and place it into your [extensions folder](http://bluej.org/extensions/extensions.html#install)
5. Start BlueJ and open the preferences
6. Under the *Libraries* tab add the *classes.jar* from your **leJOS** distribution
7. Under the *Extensions* tab set the *leJOS Folder* and *leJOS Version*
8. Restart **BlueJ**

Step-by-Step Guide
------------------

### Install Java
To use BlueJ you need to install at least version 6 of the Java Development Kit (JDK).
Download and install your package of choice from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
leJOS requires a 32 bit Java installation to work properly. So make sure to install
the x86 version of the JDK, even if you're running a 64 bit Operating System.

Please pay attention to the sidebar on the
[BlueJ website](http://bluej.org/download/download.html):
> Note that only the version labeled JDK will work. Not the one labeled JRE,
> nor the one that comes with NetBeans, nor the EE version.

and
> **Debian/Ubuntu** users should install the JDK via the package management system
> rather than using the links above. See the installation instructions. [...]
> On **MacOS X 10.5+**, a recent Java SDK version is installed by default.

### Install BlueJ
Download **BlueJ** from the [BlueJ project website](http://bluej.org/download/download.html).
For detailed instructions how to install **BlueJ** have a look at the [BlueJ installation instructions](http://bluej.org/download/install.html).

### Install leJOS
**leJOS** can be downloaded from the [project homepage](http://www.lejos.org/nxj-downloads.php).

For **Windows** get the bundled installer (`leJOS_NXJ_<version>_win32_setup.exe`)
and simply follow the steps of the installation process.

For all **other systems** get the tarball (`leJOS_NXJ_<version>.tar.gz`) and unpack
it in a folder of your choice.

The windows installer will automatically set the `NXJ_HOME` environment variable
to the installation folder. On other systems it is recommended to set it
manually. See the documentation of your operating system or
[here](http://www.google.com?q=How+to+set+environment+variables+on+UNIX) for instructions.

If you're running multiple versions of Java (especially on a 64 bit OS, where you
need to install a separate 32 bit version of Java to run leJOS on), you also should set
the `LEJOS_NXT_JAVA_HOME` variable to the correct Java version.

### Install BlueJ.leJOS
Download the [extension](jar/bluej-lejos.jar) from the *jar* folder of this repository.
You need to place the jar-file in the [extensions folder](http://bluej.org/extensions/extensions.html#install) of your **BlueJ** installation.
Depending on the operating system you are running, the location of the extension folder varies:

If you want to install the extension for every user and project on your machine:

-	**Windows**: `<BLUEJ_HOME>\lib\extensions`
-	**Mac OS**: `<BLUEJ_HOME>/BlueJ.app/Contents/Resources/Java/extensions` (Control-click BlueJ.app and choose Show Package Contents)
-	**UNIX**: `<BLUEJ_HOME>/lib/extensions`

If you want to install the extension for all projects of the current user:

-	**Windows**: `<USER_HOME>\bluej\extensions`
-	**Mac OS**: `<USER_HOME>/Library/Preferences/org.bluej/extensions`
-	**UNIX**: `<USER_HOME>/.bluej/extensions`

If you want to install the extension just for one project:

-	**All sytstems**: `<BLUEJ_PROJECT>/extensions`

Where `<BLUEJ_HOME>` is the path to your local **BlueJ** installation, `<USER_HOME>`
is the path to your home folder and `<BLUEJ_PROJECT>` is the path of the project
you want to use.

Now restart **BlueJ**, if the application is already running.

### Configure BlueJ and BlueJ.leJOS
To use **BlueJ.leJOS** you need to setup the path to your **leJOS** installation.
Open the **BlueJ** preferences and go to the *Extensions* tab. If the extension
is correctly installed, you should see a panel titled *BlueJ.leJOS*.

If you set the `NXJ_HOME` environment variable, the correct path to your **leJOS**
installation should already be set. Otherwise choose the correct folder (the folder
containing the *bin* and *lib* folders).

If you want to use an older version of **leJOS** choose it in the *leJOS Version*
box.

Now change to the *Libraries* tab. You need to add the *classes.jar* from your
**leJOS** version into the classpath of your project. Click *Add* and navigate
to the same *leJOS* folder you selected before. Find the *classes.jar*
(leJOS 0.9: *lib/nxt", leJOS 0.8: *lib*) and click *Choose*.

Close the preferences by clicking *Ok*. The extension will check your configuration
and show a warning if anything went wrong. Otherwise you need to restart **BlueJ**
one more time for the changes to take effect.

### Additional installations to connect Lego Mindstorms NXT
If you never used *Lego Mindstorms NXT* on your machine, you probably need to
install additional drivers to connect to the NXT brick via USB or Bluetooth.

On **Windows* you can use the NXT driver from the [RobotC website](http://www.robotc.net/download/nxt/). After installing either the 32 or 64 bit version you should be
good to go.

On other systems you need to get the *Fantom Driver* from the [Lego Mindstroms website](http://mindstorms.lego.com/en-us/support/files/Driver.aspx).

How to use
==========

Configuration
-------------

### General Setup

### Other options

Usage
-----

### Running leJOS programs

### Debugging leJOS programs

FAQ
===

* How can I use different *leJOS* versions for different projects

	> You can configure different settings for different projects by bundling
	> the necessary files with your project folder.
	> First create your project. Navigate to the projects folder an create
	> an *extensions* and a *+lib* folder in it. Place the extension in the
	> *extensions* folder and the *classes.jar* from the *leJOS* installation
	> into the *+lib* folder.
	> After a restart *BlueJ* should load the extension and the library
	> automatically. For more details have a look at the [BlueJ reference manual](http://bluej.org/doc/documentation.html)