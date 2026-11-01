package com.winscript.support.execution.batch;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 右键 / Run 菜单直接运行 .bat / .cmd：命中批处理文件时自动填充脚本路径。
 */
public final class BatchRunConfigurationProducer extends RunConfigurationProducer<BatchRunConfiguration> {

    public BatchRunConfigurationProducer() {
        // RunConfigurationProducer 的三个带 type/factory 参数构造器均已废弃，
        // 用 boolean 版（参数无实际作用）并覆写 getConfigurationFactory()
        super(true);
    }

    @Override
    public @NotNull ConfigurationFactory getConfigurationFactory() {
        return BatchRunConfigurationType.getInstance().getConfigurationFactories()[0];
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull BatchRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        VirtualFile file = findBatchFile(context);
        if (file == null) {
            return false;
        }
        configuration.setScriptPath(file.getPath());
        configuration.setName(file.getName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull BatchRunConfiguration configuration,
                                              @NotNull ConfigurationContext context) {
        VirtualFile file = findBatchFile(context);
        return file != null && file.getPath().equals(configuration.getScriptPath());
    }

    private static VirtualFile findBatchFile(@NotNull ConfigurationContext context) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(context.getDataContext());
        return file != null && !file.isDirectory() && isBatchFile(file) ? file : null;
    }

    static boolean isBatchFile(@NotNull VirtualFile file) {
        String extension = file.getExtension();
        return "bat".equalsIgnoreCase(extension) || "cmd".equalsIgnoreCase(extension);
    }
}
