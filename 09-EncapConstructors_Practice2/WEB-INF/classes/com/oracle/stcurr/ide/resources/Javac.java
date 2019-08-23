/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.resources;

import com.oracle.stcurr.ide.ast.scanners.FindMainMethodsScanner;
import com.oracle.stcurr.ide.resources.json.Names;
import com.oracle.stcurr.ide.resources.json.Project;
import com.oracle.stcurr.ide.util.ArgSplitter;
import com.oracle.stcurr.ide.util.Utils;
import com.oracle.stcurr.ide.web.ConfigBean;
import com.oracle.stcurr.util.CommandCallable;
import com.oracle.stcurr.util.CommandExecutorService;
import com.oracle.stcurr.util.CommandResult;
import com.oracle.stcurr.util.LXCCommandCallable;
import com.sun.source.tree.CompilationUnitTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.servlet.ServletContext;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

/**
 *
 * @author mheimer
 */
@Path("/javac")
@Produces({"text/plain"})
@Consumes({"application/json"})
@RequestScoped
public class Javac {

    private static final Logger logger = Logger.getLogger("com.oracle.stcurr.ide.resources");
    @Inject
    private ConfigBean configBean;
    @Inject
    private CommandExecutorService ces;
    @Context
    ServletContext context;
//    private static Pattern shortFileNamePattern = Pattern.compile("(.+[\\.\\\\/])*?(.+?)([\\.\\\\/]java)??");
//    private static Pattern shortClassNamePattern = Pattern.compile("(.+\\.)*(.+?)");

    //TODO create zip of project (some unique url that deletes after download or expires)
    @GET
    @Produces({"application/json"})
    @Path("sample-project")
    public Project getSampleProject() {
        Project project = new Project();
        project.put("Test1.java", "public class Test1 { String s = \"hi\"}");
        project.put("Test2.java", "public class Test2 { String s = \"hi\"}");
        project.setSelectedFile(0);
        return project;
    }

    @POST
    @Produces({"application/json"})
    @Path("sample-project")
    public void testSampleProject(Project project) {
        System.out.println(project);
    }

    @GET
    public String getInfo() {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        Set<SourceVersion> versions = javaCompiler.getSourceVersions();
        StringBuilder sb = new StringBuilder();
        sb.append(javaCompiler.getClass().toString());
        sb.append(" supported versions: ");
        for (SourceVersion sourceVersion : versions) {
            sb.append(sourceVersion);
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @POST
    @Consumes({"text/plain"})
    @Path("qualified-name-validator")
    public String validateQualifiedName(String identifier) {
        return Boolean.toString(SourceVersion.isName(identifier));
    }

    @POST
    @Consumes({"text/plain"})
    @Path("short-name-validator")
    public String validateShortName(String identifier) {
        boolean result = !SourceVersion.isKeyword(identifier);
        result &= SourceVersion.isIdentifier(identifier);
        return Boolean.toString(result);
    }

    @POST
    @Path("name-validator")
    public String validateNames(Names names) {
        boolean result = !SourceVersion.isKeyword(names.getName());
        result &= SourceVersion.isIdentifier(names.getName());
        if (!result) {
            return "name";
        }
        result &= SourceVersion.isName(names.getPkg());
        if (!result) {
            return "pkg";
        }
        return Boolean.toString(result);
    }

    @POST
    @Path("project")
    public String compileAndRun(final Project project) throws IOException {
        java.nio.file.Path projectPath = Files.createTempDirectory("project-");
        System.out.println("Compiling to: " + projectPath);

        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
//        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(diagnosticCollector, null, null);
        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        File projectDir = projectPath.toFile();
        javaFileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(projectDir));

        Iterable<? extends JavaFileObject> compilationUnits = Utils.getCompilationUnits(Arrays.asList(project.getFiles()));
        int fileCount = 0;
        Iterator i = compilationUnits.iterator();
        while (i.hasNext()) {
            fileCount++;
            i.next();
        }

        CompilationTask task = javaCompiler.getTask(null, javaFileManager, diagnosticCollector, null, null, compilationUnits);

//        List<Processor> processors = new ArrayList<>();
//        FindMains mains = new FindMains();
//        processors.add(mains);
//        task.setProcessors(processors);


        com.sun.source.util.JavacTask javacTask = (com.sun.source.util.JavacTask) task;
        com.sun.source.util.Trees trees = com.sun.source.util.Trees.instance(task);


        //NodeConvertingScanner visitor = new NodeConvertingScanner();
        FindMainMethodsScanner mains = new FindMainMethodsScanner(javacTask);

////            final Iterable<? extends com.sun.source.tree.CompilationUnitTree> parsedTrees = javacTask.parse();
////            for(com.sun.source.tree.CompilationUnitTree parsedTree : parsedTrees) {
////                //parsedTree.accept(visitor, null);
////                visitor.scan(parsedTree, null);
////            }




        Iterable<? extends javax.lang.model.element.Element> analyzedTrees = javacTask.analyze();

        Set<CompilationUnitTree> compilationUnitTrees = new HashSet<>();
        for (javax.lang.model.element.Element element : analyzedTrees) {
            compilationUnitTrees.add(trees.getPath(element).getCompilationUnit());
        }
        for (CompilationUnitTree compilationUnitTree : compilationUnitTrees) {
            mains.scan(compilationUnitTree, null);
        }


        

        StringBuilder output = new StringBuilder();
        if (diagnosticCollector.getDiagnostics().size() > 0) {
            javaFileManager.close();
            for (Diagnostic diagnostic : diagnosticCollector.getDiagnostics()) {
                output.append(diagnostic.toString()).append("\n");
                System.out.format("Error on line %d in %s%n",
                        diagnostic.getLineNumber(), diagnostic.getSource());
                System.out.println(diagnostic.toString());
                System.out.println(diagnostic.getCode());
                System.out.println(diagnostic.getSource());
                System.out.println(diagnostic.getStartPosition());
                System.out.println(diagnostic.getEndPosition());
                System.out.println(diagnostic.getColumnNumber());
                System.out.println(diagnostic.getCode());
                System.out.println(diagnostic.getKind());
                System.out.println(diagnostic.getMessage(null));
            }
        } else {
            Iterable<? extends JavaFileObject> generatedFiles = javacTask.generate();
            javaFileManager.close();
            List<String> cmd = new ArrayList<>();
            String mainClass = null;
            Map<String,List<String>> classesWithMainMethods = mains.getClassesWithMainMethods();
            if (classesWithMainMethods.keySet().size() == 1 && classesWithMainMethods.values().iterator().next().size() == 1) {
                mainClass = classesWithMainMethods.values().iterator().next().get(0);
            } else if (mains.getClassesWithMainMethods().size() > 1) {
                if (project.getSelectedFile() != -1 && project.getFiles()[project.getSelectedFile()].getName().endsWith(".java")) {
                    List<String> mainsInFile = mains.getClassesWithMainMethods().get("/" + project.getFiles()[project.getSelectedFile()].getName());
                    if(mainsInFile.size() == 1) {
                        mainClass = mainsInFile.get(0);
                    }
                    
//                    Matcher m = shortFileNamePattern.matcher(project.getFiles()[project.getSelectedFile()].getName());
//                    m.matches();
//                    String shortFileName = m.group(2);
//                    for (String type : mains.getClassesWithMainMethods()) {
//                        m = shortClassNamePattern.matcher(type);
//                        m.matches();
//                        if (shortFileName.equals(m.group(2)) && mainClass != null) {
//                            mainClass = null; //two or more possible matches
//                            break;
//                        } else if (shortFileName.equals(m.group(2))) {
//                            mainClass = type;
//                        }
//                    }
                }
            }
            if (mainClass == null) {
                if (mains.getClassesWithMainMethods().isEmpty()) {
                    output.append("Error: Could not find or load main class\n");
                    output.append("A public static void main(String[] args) {} method is required\n");
                } else {
                output.append("Error: Could not determine main class\n");
                output.append("Please select a tab belonging to a uniquely named main class before running\n");
                }
            } else {
                cmd.add("java");
                if (configBean.getPolicyFilePath().length() > 0) {
                    java.nio.file.Path policyPath = Files.createTempFile(projectDir.toPath(), "security", ".policy");
                    PrintWriter out = new PrintWriter(policyPath.toFile());

                    InputStream in = context.getResourceAsStream(configBean.getPolicyFilePath());
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String s = br.readLine();
                    String replacementPath;
                    if (configBean.isEnableLXC()) {
                        replacementPath = "\\${user.home}";
                    } else {
                        replacementPath = projectDir.getAbsolutePath();
                    }
                    while (s != null) {
                        s = s.replaceAll("\\{\\{project\\.dir}}", replacementPath);
                        out.println(s);
                        s = br.readLine();
                    }
                    out.close();
                    in.close();
                    cmd.add("-Djava.security.manager");
                    if (configBean.isEnableLXC()) {
                        cmd.add("-Djava.security.policy=" + "/home/java/" + policyPath.getFileName());
                    } else {
                        cmd.add("-Djava.security.policy=" + policyPath.toAbsolutePath());
                    }
                }
                cmd.add(mainClass);
                List<String> argParts = ArgSplitter.splitArgs(project.getArgs());
                System.out.println("Args Count: " + argParts.size());
                for(String s : argParts) {
                    System.out.println("#" + s + "#");
                }
                cmd.addAll(argParts);

                System.out.println("CMD: " + cmd);

                CommandCallable cc;
                if (configBean.isEnableLXC()) {
                    cc = new LXCCommandCallable(cmd, projectDir);
                } else {
                    cc = new CommandCallable(cmd, projectDir);
                }
                Future<CommandResult> f = ces.submit(cc);
                CommandResult runResult;
                try {
                    runResult = f.get();
                    output.append(runResult.getOutputBuffer());
                    if (runResult.getReturnCode() != 0) {
                        output.append("\n");
                        output.append("Process execution failed or did not complete in allocated time.");
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    output.append("\n");
                    output.append("Process execution failed. Please try again.\n");
                    output.append(ex.getMessage());
                    output.append("\n");
                    for (StackTraceElement ste : ex.getStackTrace()) {
                        output.append(ste.toString());
                        output.append("\n");
                    }
                    if (ex.getCause() != null) {
                        output.append(ex.getCause().getMessage());
                        output.append("\n");
                        for (StackTraceElement ste : ex.getCause().getStackTrace()) {
                            output.append(ste.toString());
                            output.append("\n");
                        }
                    }
                    if (ex.getCause().getCause() != null) {
                        output.append(ex.getCause().getCause().getMessage());
                        output.append("\n");
                        for (StackTraceElement ste : ex.getCause().getCause().getStackTrace()) {
                            output.append(ste.toString());
                            output.append("\n");
                        }
                    }
                }
            }
        }
        
        if (projectDir.exists() && projectDir.isDirectory()) {
            Utils.recursiveDelete(projectDir);
        }
        if (output.length() < 1) {
            output.append("\n");
        }
        return output.toString();
//        ResponseBuilder rb = Response.ok();
//        return rb.build();
    }
}
