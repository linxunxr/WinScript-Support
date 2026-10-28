package com.winscript.support.execution.powershell;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * 右键 / Run 菜单直接运行 .ps1：命中 PowerShell 文件时自动填充脚本路径。
 */
public final class PowerShellRunConfigurationProducer
        extends RunConfigurationProducer<PowerShellRunConfiguration> {

    public PowerShellRunConfigurationProducer() {
        super(PowerShellRunConfigurationType.getInstance().getConfigurationFactories()[0]);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull PowerShellRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        VirtualFile file = findPowerShellFile(context);
        if (file == null) {
            return false;
        }
        configuration.setScriptPath(file.getPath());
        configuration.setName(file.getName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull PowerShellRunConfiguration configuration,
                                              @NotNull ConfigurationContext context) {
        VirtualFile file = findPowerShellFile(context);
        return file != null && file.getPath().equals(configuration.getScriptPath());
    }

    private static VirtualFile findPowerShellFile(@NotNull ConfigurationContext context) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(context.getDataContext());
        return file != null && !file.isDirectory() && "ps1".equalsIgnoreCase(file.getExtension()) ? file : null;
    }
}
