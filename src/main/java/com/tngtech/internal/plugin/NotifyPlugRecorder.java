package com.tngtech.internal.plugin;

import com.tngtech.internal.plug.Plug;
import com.tngtech.internal.plug.PlugConfig;
import com.tngtech.internal.plug.PlugSender;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("UnusedDeclaration")
public class NotifyPlugRecorder extends Recorder {
    private final String plugNumber;

    @DataBoundConstructor
    public NotifyPlugRecorder(String plugNumber) {
        this.plugNumber = plugNumber;
    }

    public String getPlugNumber() {
        return plugNumber;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {
        Result result = build.getResult();

        if (result.toString().equals(Result.SUCCESS.toString())) {
            return true;
        }

        PlugConfig plugConfig = new PlugConfig(getDescriptor().getHostName(), getDescriptor().getHostPort(),
                getDescriptor().getAdminAccount(), getDescriptor().getAdminPassword(), getPlugNumber());
        new PlugSender().send(listener, plugConfig);
        return true;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private Properties properties;

        private String hostName = getDefaultHostName();
        private int hostPort = Integer.parseInt(getDefaultHostPort());

        private String adminAccount = getDefaultAdminAccount();
        private String adminPassword = getDefaultAdminPassword();

        public DescriptorImpl() {
            super(NotifyPlugRecorder.class);
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.text_description();
        }

        public String getDefaultHostName() {
            return Messages.defaults_host_name();
        }

        public String getDefaultHostPort() {
            return Messages.defaults_host_port();
        }

        public String getDefaultAdminAccount() {
            return Messages.defaults_admin_account();
        }

        public String getDefaultAdminPassword() {
            return Messages.defaults_admin_password();
        }

        public FormValidation doCheckHostName(@QueryParameter String hostName) {
            return checkForExistence(hostName);
        }

        public FormValidation doCheckHostPort(@QueryParameter String hostPort) {
            return checkForNumber(hostPort);
        }

        public FormValidation doCheckAdminAccount(@QueryParameter String adminAccount) {
            return checkForExistence(adminAccount);
        }

        public FormValidation doCheckAdminPassword(@QueryParameter String adminPassword) {
            return checkForExistence(adminPassword);
        }

        public ListBoxModel doFillPlugNumberItems() {
            ListBoxModel items = new ListBoxModel();
            for (Plug plug : Plug.values()) {
                String plugName = plug.name();
                String plugText = plug.getPlugNumber().toString();
                items.add(new ListBoxModel.Option(plugText, plugName));
            }
            return items;
        }

        private FormValidation checkForExistence(String value) {
            if (value.length() == 0)
                return FormValidation.error(Messages.error_notEntered());
            if (value.length() < 4)
                return FormValidation.warning(Messages.warning_tooShort());
            return FormValidation.ok();
        }

        private FormValidation checkForNumber(String numberText) {
            try {
                Integer.parseInt(numberText);
                return FormValidation.ok();
            } catch (NumberFormatException e) {
                return FormValidation.error(Messages.error_noNumber());
            }
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            hostName = formData.getString("hostName");
            hostPort = formData.getInt("hostPort");
            adminAccount = formData.getString("adminAccount");
            adminPassword = formData.getString("adminPassword");

            save();
            return super.configure(req, formData);
        }

        public String getHostName() {
            return hostName;
        }

        public int getHostPort() {
            return hostPort;
        }

        public String getAdminAccount() {
            return adminAccount;
        }

        public String getAdminPassword() {
            return adminPassword;
        }
    }
}