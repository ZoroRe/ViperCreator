package me.android.viper;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.android.viper.utils.FileConfig;
import me.android.viper.utils.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static me.android.viper.utils.FileUtils.writeJavaFile;
import static me.android.viper.utils.FileUtils.writeXmlFile;

/**
 * Created by luguanquan on 2018/8/13.
 */
public class ViperCreateAction extends AnAction {


    private Project project;
    private String packageName = "";
    private String moduleName = "";
    private String rootPackage = "";
    private String path = "";

    public enum CreateType {
        BaseViper, Activity, Fragment, Di, Data,
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        VirtualFile selectGroup = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        path = selectGroup.getPath();
        packageName = path.substring(path.indexOf("java") + 5, path.length()).replace("/", ".");
        initAppPackage();
        showDialog(e.getProject());
    }

    private void refreshProject(Project project) {
        if (project != null) {
            project.getBaseDir().refresh(false, true);
        }
    }

    private void showDialog(Project project) {
        ViperDialog dialog = new ViperDialog(((moduleName, type) -> {
            ViperCreateAction.this.moduleName = moduleName;
            switch (type) {
                case BaseViper:
                    createBaseViper();
                    break;
                case Data:
                    createData();
                    break;
                case Di:
                    createDi();
                    break;
                case Activity:
                    createActivityViper();
                    break;
                case Fragment:
                    createFragmentViper();
                    break;
            }
            refreshProject(project);
        }));
        dialog.setVisible(true);
    }


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


    private void createBaseViper() {
        String curPkgPath = getCurrentPath();
        //base/viper
        //Assembler.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_ASSMBLER)), curPkgPath + "/base/viper", "Assembler");
        //BaseInteractor.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_BASE_INTERACTOR)), curPkgPath + "/base/viper", "BaseInteractor");
        //IBaseInteractor.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_IBASE_INTERACTOR)), curPkgPath + "/base/viper", "IBaseInteractor");
        //BasePresenter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_BASE_PRESENTER)), curPkgPath + "/base/viper", "BasePresenter");
        //IBasePresenter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_IBASE_PRESENTER)), curPkgPath + "/base/viper", "IBasePresenter");
        //IBaseRouter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_IBASE_ROUTER)), curPkgPath + "/base/viper", "IBaseRouter");
        //BaseRouter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_BASE_ROUTER)), curPkgPath + "/base/viper", "BaseRouter");
        //IBaseView.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_IBASE_VIEW)), curPkgPath + "/base/viper", "IBaseView");
        //BaseViewModel.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE_VIPER + FileConfig.FILE_BASE_VIEW_MODEL)), curPkgPath + "/base/viper", "BaseViewModel");
        //base
        //DiActivity.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE + FileConfig.FILE_DI_ACTIVITY)), curPkgPath + "/base", "DiActivity");
        //DiFragment.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE + FileConfig.FILE_DI_FRAGMENT)), curPkgPath + "/base", "DiFragment");
        //BaseActivity.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_BASE + FileConfig.FILE_DI_BASE_ACTIVITY)), curPkgPath + "/base", "BaseActivity");

    }

    private void createData() {
        String rootPkgPath = getRootPkgPath();
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA + FileConfig.FILE_DATA_MANAGER)), rootPkgPath + "/data", "DataManager");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA + FileConfig.FILE_APP_DATA_MANAGER)), rootPkgPath + "/data", "AppDataManager");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA + FileConfig.FILE_APP_DATA_MANAGER_MODULE)), rootPkgPath + "/data", "AppDataManagerModule");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA_DB + FileConfig.FILE_DATA_DB_HELPER)), rootPkgPath + "/data/db", "DbHelper");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA_SP + FileConfig.FILE_DATA_SP_HELPER)), rootPkgPath + "/data/prefs", "PreferencesHelper");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DATA_REMOTE + FileConfig.FILE_DATA_API_HELPER)), rootPkgPath + "/data/remote", "ApiHelper");
    }

    private void createDi() {
        String rootPkgPath = getRootPkgPath();
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_BUILDER + FileConfig.FILE_DI_ACTIVITY_BUILDER)), rootPkgPath + "/di/builder", "ActivityBuilder");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_COMPONENT + FileConfig.FILE_DI_APP_COMPONENT)), rootPkgPath + "/di/component", "AppComponent");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_COMPONENT + FileConfig.FILE_DI_APP_DATA_MANAGER_COMPONENT)), rootPkgPath + "/di/component", "AppDataManagerComponent");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_MODULE + FileConfig.FILE_DI_APP_MODULE)), rootPkgPath + "/di/module", "AppModule");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_MODULE + FileConfig.FILE_DI_NET_MODULE)), rootPkgPath + "/di/module", "NetModule");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_MODULE + FileConfig.FILE_DI_SUB_MODULE)), rootPkgPath + "/di/module", "SubModule");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_SCOPE + FileConfig.FILE_DI_ACTIVITY_SCOPE)), rootPkgPath + "/di/scope", "ActivityScope");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_SCOPE + FileConfig.FILE_DI_DATA_SCOPE)), rootPkgPath + "/di/scope", "DataScope");
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_DI_SCOPE + FileConfig.FILE_DI_FRAGMENT_SCOPE)), rootPkgPath + "/di/scope", "FragmentScope");
    }

    private void createActivityViper() {
        String prefix = capitalize(moduleName);
        String module = moduleName.toLowerCase();
        //${Module}Interactor.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_INTERACTOR)), getCurrentPath() + "/" + module + "/interactor", prefix + "Interactor");
        //${Module}Presenter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_PRESENTER)), getCurrentPath() + "/" + module + "/presenter", prefix + "Presenter");
        //${Module}Router.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_ROUTER)), getCurrentPath() + "/" + module + "/router", prefix + "Router");
        //${Module}ViewModel.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_VIEW_MODULE)), getCurrentPath() + "/" + module + "/view", prefix + "ViewModel");
        //${Module}Activity.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_ACTIVITY)), getCurrentPath() + "/" + module, prefix + "Activity");
        //${Module}ActivityModule.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_ACTIVITY_MODULE)), getCurrentPath() + "/" + module, prefix + "ActivityModule");
        //${module}_activity.xml
        writeXmlFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_LAYOUT + FileConfig.FILE_LAYOUT_ACTIVITY)), getLayoutPath(), module + "_activity");

    }

    private void createFragmentViper() {
        String prefix = capitalize(moduleName);
        String module = moduleName.toLowerCase();
        //${Module}Interactor.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_INTERACTOR)), getCurrentPath() + "/" + module + "/interactor", prefix + "Interactor");
        //${Module}Presenter.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_PRESENTER)), getCurrentPath() + "/" + module + "/presenter", prefix + "Presenter");
        //${Module}Router.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_ROUTER)), getCurrentPath() + "/" + module + "/router", prefix + "Router");
        //${Module}ViewModel.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_VIEW_MODULE)), getCurrentPath() + "/" + module + "/view", prefix + "ViewModel");
        //${Module}Fragment.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_FRAGMENT)), getCurrentPath() + "/" + module, prefix + "Fragment");
        //${Module}FragmentModule.java
        writeJavaFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_ROOT + FileConfig.FILE_FRAGMENT_MODULE)), getCurrentPath() + "/" + module, prefix + "FragmentModule");
        //${module}_fragment.xml
        writeXmlFile(replaceVariable(readTemplateFile(FileConfig.DIR_TEMPLATE_LAYOUT + FileConfig.FILE_LAYOUT_FRAGMENT)), getLayoutPath(), module + "_fragment");

    }

    private String getCurrentPath() {
        String packagePath = packageName.replace(".", "/");
        String appPath = project.getBasePath() + "/app/src/main/java/" + packagePath + "/";
        return appPath;
    }

    private String getRootPkgPath() {
        String rootPkgPath = rootPackage.replace(".", "/");
        return project.getBasePath() + "/app/src/main/java/" + rootPkgPath + "/";
    }

    private String getLayoutPath() {
        return project.getBasePath() + "/app/src/main/res/layout/";
    }

    private void initAppPackage() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/App/src/main/AndroidManifest.xml");

            NodeList nodeList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;
                rootPackage = element.getAttribute("package");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readTemplateFile(String file) {
        return FileUtils.streamToString(this.getClass().getResourceAsStream(file));
    }

    private String capitalize(String content) {
        if (content == null || content.length() == 0) {
            return content;
        }
        return content.toUpperCase().substring(0, 1) + content.toLowerCase().substring(1);
    }
}
