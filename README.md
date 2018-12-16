# ViperCreator

An Android Studio plugin for Viper Architecture.

### Download Plugin

[ViperCreator.jar](https://github.com/ZoroRe/ViperCreator/blob/master/ViperCreator.jar)

### Install

Download ViperCreator.jar , Open Android Studio, select Preferences -> Plugins -> Install plugin from disk, Select the jar .

### About Viper

I will show an android sample project base on viper later.

### Customized Your Project Package

This plugin will create some java code base on Base Viper package, if you project base viper package are diffrent, you should change some package in code and rebuild the plugin. See ViperCreateAction.java replaceVariable.

```java
    /**
     * =========== start of item need to replace ============
     * ${root} root package name
     * ${package} current package
     * ${data}  package name of DataManager
     * ${base} package name of Base Viper
     * ${module} new module name with lowcase
     * ${Module} new module name with capitalize
     * ${di}  package of di
     * ${di_builder} package of builder in di
     * ${di_component} package of component in di
     * ${di_module} package of module in di
     * ${di_scope} package of scope in di
     * =========== end of item need to replace ============
     *
     * @param content
     * @return
     */
    private String replaceVariable(String content) {
        if (content == null) {
            return null;
        }
        String temp = content;
        if (moduleName != null && moduleName.length() > 0) {
            temp = temp.replace("${Module}", capitalize(moduleName))
                    .replace("${module}", moduleName.toLowerCase());
        }
        //注意,某些目录不是自动识别的,如果不符合直接在代码改重新编译一个,比如这里默认了
        // base 是在 应用包名.ui 下
        // di, data 在 应用包名下
        return temp
                .replace("${root}", rootPackage)
                .replace("${package}", packageName)
                .replace("${data}", rootPackage + ".data")
                .replace("${base}", rootPackage + ".ui.base")
                .replace("${di}", rootPackage + ".di")
                .replace("${di_builder}", rootPackage + ".di.builder")
                .replace("${di_component}", rootPackage + ".di.component")
                .replace("${di_module}", rootPackage + ".di.module")
                .replace("${di_scope}", rootPackage + ".di.scope");

    }
```

### LICENSE

Apache License 2.0
