# ClassDependenceAnalyser

A tool for java classes dependence analysis


# Purpose

This is the way to sovle the [main dex capacity exceeded](http://ct2wj.com/2015/12/22/the-way-to-solve-main-dex-capacity-exceeded-in-Android-gradle-build/) problem in google's multidex library. We can use it to generate a better maindexlist file in order to keep the size of main dex minimum.


# How to use

Example:

	gradle run -PappArgs=-e,/your/path1/here/root.jar,/your/path2/here/allclasses.jar

You can get more info:

	gradle run -PappArgs=-h

And you'll find:

```
Usage: jda [arguments] targetClass [dependenceJarPath]...
find and print the input class dependence.
Example: jda -c,android.app.Application,/your_path/android.jar
         jda -e,/your_path/rootclass.jar,/your_path/allclasses.jar

The arguments are:

   -c, --class    analysis class dependence from single class file or from jar path
   -e, --external analysis class dependence external from <clinit> and innerClass
   -h, --help     print jda help info
   -v, --version  print jda version
```
	
# License

This library is licensed under GPL license. See LICENSE for details.
