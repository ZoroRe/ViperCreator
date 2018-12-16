# ViperCreator

An Android Studio plugin for Viper Architecture.

### Install

Download ViperCreator.jar , Open Android Studio, select Preferences -> Plugins -> Install plugin from disk, Select the jar .

### About Viper

I will show an android sample project base on viper later.

### Customized Your Project Package

This plugin will create some java code base on base viper package, if you project base viper package are diffrent, you shou change some package in code and rebuild the plugin. See ViperCreateAction.java replaceVariable.

```java
    /**
     * =========== start of item need to replace ============
     * ${root} 项目根包名
     * ${package} 当前创建的文件包名
     * ${data}  DataManager 所在包名
     * ${base} viper 的 base 包名
     * ${module} 当前模块名，小写开头
     * ${Module} 当前模块名，大写开头
     * ${di}  dagger 配置包名
     * ${di_builder}
     * ${di_component}
     * ${di_module}
     * ${di_scope}
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
