package org.conjur.jenkins.conjursecrets;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.StreamTaskListener;
import org.jenkinsci.plugins.credentialsbinding.BindingDescriptor;
import org.jenkinsci.plugins.credentialsbinding.MultiBinding.MultiEnvironment;
import org.jenkinsci.plugins.credentialsbinding.impl.CredentialNotFoundException;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConjurSecretUsernameSSHKeyCredentialsBindingTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    private BindingDescriptor<ConjurSecretUsernameSSHKeyCredentials> bindCred = null;

    @Before
    public void init() {
        bindCred = new ConjurSecretUsernameSSHKeyCredentialsBinding.DescriptorImpl();
    }

    @Test
    public void testBind() throws Exception {
        ConjurSecretUsernameSSHKeyCredentialsBinding bind = new ConjurSecretUsernameSSHKeyCredentialsBinding(
                "Dev-Team-1");
        File file = new File("/var/jenkins_home/workspace/Dev-Team-1/test-pipeline");
        TaskListener task = new StreamTaskListener(System.out, Charset.defaultCharset());
        Launcher launch = j.jenkins.createLauncher(task);
        FilePath ws = new FilePath(file);
        WorkflowJob job = j.createProject(WorkflowJob.class, "test-pipeline");
        Run<?, ?> completedBuild = new WorkflowRun(job);
        MultiEnvironment env1 = null;
        try {
            env1 = bind.bind(completedBuild, ws, launch, task);
        } catch (CredentialNotFoundException ex) {
            assertThrows(CredentialNotFoundException.class, () -> bind.bind(completedBuild, ws, launch, task));
        } catch (Exception ignored) {

        }

        assertNull(env1);
    }

    @Test
    public void testUsernameVariable() throws SecurityException {
        final ConjurSecretUsernameSSHKeyCredentialsBinding userNameCredentials = new ConjurSecretUsernameSSHKeyCredentialsBinding(
                "Test pipeline");
        String usernameVariable = "userName";
        userNameCredentials.setUsernameVariable(usernameVariable);
        try {
            assertNotNull(userNameCredentials.getUsernameVariable());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPasswordVariable() throws SecurityException {
        final ConjurSecretUsernameSSHKeyCredentialsBinding secretCredentials = new ConjurSecretUsernameSSHKeyCredentialsBinding(
                "Test pipeline");
        String pwdVariable = "passwordVariable";
        secretCredentials.setSecretVariable(pwdVariable);
        try {
            assertNotNull(secretCredentials.getSecretVariable());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVariables() {
        ConjurSecretUsernameSSHKeyCredentialsBinding bind = new ConjurSecretUsernameSSHKeyCredentialsBinding(
                "Dev-Team-1");
        String userNameVar = "test";
        String pwdVar = "pwd";
        bind.setSecretVariable(pwdVar);
        bind.setUsernameVariable(userNameVar);
        Set<String> varSet = new HashSet<>(Arrays.asList(userNameVar, pwdVar));
        Set<String> actualVarSet = bind.variables();

        assertEquals(varSet, actualVarSet);
    }

    @Test
    public void testGetDisplayName() {
        assertNotNull(bindCred.getDisplayName());
    }

    @Test
    public void testRequiresWorkspace() {
        assertFalse(bindCred.requiresWorkspace());
    }

    @Test
    public void testtype() {
        ConjurSecretUsernameSSHKeyCredentialsBinding bind = new ConjurSecretUsernameSSHKeyCredentialsBinding(
                "Dev-Team-1");
        assertNotNull(bind.type());
    }

}