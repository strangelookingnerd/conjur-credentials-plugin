package org.conjur.jenkins.conjursecrets;

import hudson.FilePath;
import org.conjur.jenkins.conjursecrets.ConjurSecretFileCredentialsBinding.DescriptorImpl;
import org.jenkinsci.plugins.credentialsbinding.MultiBinding.MultiEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConjurSecretFileCredentialsBindingTest {

    private static final String CREDENTIAL_ID = "cred-id";

    private ConjurSecretFileCredentialsBinding binding;

    @Mock
    private ConjurSecretFileCredentialsBinding mockBinding;

    @Before
    public void setUp() throws Exception, SecurityException {
        binding = new ConjurSecretFileCredentialsBinding(CREDENTIAL_ID);
    }

    @Test
    public void testType() {
        assertNotNull(binding.type());
    }

    @Test
    public void testGetFileVariable() {
        String fileName = "file-name";
        binding.setFileVariable(fileName);

        assertEquals("file-name", binding.getFileVariable());
    }

    @Test
    public void testGetContentVariable() {
        String contentName = "content-name";
        binding.setContentVariable(contentName);

        assertEquals("content-name", binding.getContentVariable());
    }

    @Test
    public void testBind1() throws IOException, InterruptedException {
        Map<String, String> secretVals = new HashMap<>();
        MultiEnvironment env = new MultiEnvironment(secretVals);
        when(mockBinding.bind(any(), any(), any(), any())).thenReturn(env);

        assertInstanceOf(MultiEnvironment.class, mockBinding.bind(any(), any(), any(), any()));
    }

    @Test
    public void testVariables() {
        Set<String> varSet = new HashSet<String>();
        String variable = "test-file";
        String contentVar = "content-test";
        varSet.add(variable);
        varSet.add(contentVar);
        binding.setFileVariable(variable);
        binding.setContentVariable(contentVar);
        Set<String> actualVarSet = binding.variables();

        assertEquals(varSet, actualVarSet);
    }

    @Test
    public void testDescriptorImplReturnsCorrectDisplayNameAndType() {
        DescriptorImpl descriptor = new DescriptorImpl();

        assertEquals("Conjur Secret File Credentials", descriptor.getDisplayName());
        assertEquals(ConjurSecretFileCredentials.class, descriptor.type());
        assertTrue(descriptor.requiresWorkspace());
    }

    @Test
    public void testCleanupAction() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        FilePath mockPath = mock(FilePath.class);
        when(mockPath.getRemote()).thenReturn("/test/job");
        Class<?> cleanupActionClass = Class.forName("org.conjur.jenkins.conjursecrets.ConjurSecretFileCredentialsBinding$CleanupAction");
        Constructor<?> constructor = cleanupActionClass.getDeclaredConstructor(
                hudson.FilePath.class);
        constructor.setAccessible(true);
        Object cleanupAction = constructor.newInstance(mockPath);
        Method getPathMethod = cleanupActionClass.getDeclaredMethod("getPath");
        getPathMethod.setAccessible(true);
        String path = (String) getPathMethod.invoke(cleanupAction);

        assertEquals("/test/job", path);
    }

}
