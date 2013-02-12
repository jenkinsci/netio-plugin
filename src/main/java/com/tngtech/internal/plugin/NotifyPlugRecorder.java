package com.tngtech.internal.plugin;

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
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class NotifyPlugRecorder extends Recorder {

    @DataBoundConstructor
    public NotifyPlugRecorder() {
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

        String output = String.format("Using connection to %s:%s@%s:%d", getDescriptor().getAdminAccount(),
                getDescriptor().getAdminPassword(), getDescriptor().getHostName(), getDescriptor().getHostPort());
        listener.getLogger().println(output);

        return true;
    }

    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
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

        // TODO load properties file
        public String getDefaultHostName() {
            return "netio.io";
        }

        public String getDefaultHostPort() {
            return Integer.toString(80);
        }

        public String getDefaultAdminAccount() {
            return "admin";
        }

        public String getDefaultAdminPassword() {
            return "admin";
        }

        public FormValidation doCheckHostName(@QueryParameter String hostName) {
            return checkForExistence(hostName);
        }

        public FormValidation doCheckHostPort(@QueryParameter String hostPortText) {
            return checkForNumber(hostPortText);
        }

        public FormValidation doCheckAdminAccount(@QueryParameter String adminAccount) {
            return checkForExistence(adminAccount);
        }

        public FormValidation doCheckAdminPassword(@QueryParameter String adminPassword) {
            return checkForExistence(adminPassword);
        }

        private FormValidation checkForExistence(String value) {
            if (value.length() == 0)
                return FormValidation.error(Messages.error_notEntered());
            if (value.length() < 4)
                return FormValidation.warning(Messages.warning_tooShort());
            return FormValidation.ok();
        }

        //TODO does not work
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