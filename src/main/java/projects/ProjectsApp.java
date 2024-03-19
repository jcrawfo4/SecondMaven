package projects;

import entity.Project;
import projects.exceptions.DbException;
import service.ProjectService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ProjectsApp {

    private final Scanner scanner = new Scanner(System.in);
    private final ProjectService projectService = new ProjectService();
    private Project currentProject;
    private final List<String> operations = List.of(
            "1) Add a project",
            "2) Fetch all projects",
            "3) Select a project",
            "4) Update project details",
            "5) Delete a project");


    private void processUserSelections() {
        boolean done = false;
        while (!done) {
            try {
                int selection = getUserSelection();
                switch (selection) {
                    case -1:
                        done = exitMenu();
                        break;
                    case 1:
                        createProject();
                        break;
                    case 2:
                        listProjects();
                        break;
                    case 3:
                        selectProject();
                        break;
                    case 4:
                        updateProject();
                        break;
                    case 5:
                        deleteProject();
                        break;
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.printf("Invalid input. Please try again.%s%n", e.getMessage());
            }

        }
    }

    private void deleteProject() {
        listProjects();
        Integer projectId = getIntInput("Enter the project ID of the project you want to delete.");
        String projectName = null;
        try {
            projectName = projectService.fetchProjectById(projectId).getProjectName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        projectService.deleteProject(projectId);
        System.out.println(projectId + ": " + projectName + " has been deleted.");
        if(Objects.nonNull(currentProject) && currentProject.getProjectId().equals(projectId)){
            currentProject = null;
        }
    }

    private void updateProject() {
        listProjects();
        Integer projectId = getIntInput("Enter the project ID of the project you want to update.");
        currentProject = projectService.fetchProjectById(projectId);
        if (Objects.isNull(currentProject)) {
            System.out.println("You must select a project first.");
        }
        String projectName = getStringInput("Enter the new project name ["
                + currentProject.getProjectName() + "]");
        String estimatedHours = getStringInput("Enter the new estimated hours ["
                + currentProject.getEstimatedHours() + "]");
        assert estimatedHours != null;
        BigDecimal estimatedHoursDecimal = new BigDecimal(estimatedHours);
        String actualHours = getStringInput("Enter the new actual hours ["
                + currentProject.getActualHours() + "]");
        assert actualHours != null;
        BigDecimal actualHoursDecimal = new BigDecimal(actualHours);
        Integer difficulty = getIntInput("Enter the new project difficulty (1-5) ["
                + currentProject.getDifficulty() + "]");
        String notes = getStringInput("Enter the new project notes ["
                + currentProject.getNotes() + "]");
        Project project = new Project();
        project.setProjectId(currentProject.getProjectId());
        project.setProjectName(Objects.isNull(projectName) ? currentProject.getProjectName() : projectName);
        project.setEstimatedHours(estimatedHoursDecimal);
        project.setActualHours(actualHoursDecimal);
        project.setDifficulty(Objects.isNull(difficulty) ? currentProject.getDifficulty() : difficulty);
        project.setNotes(Objects.isNull(notes) ? currentProject.getNotes() : notes);

        boolean dbProject = projectService.modifyProjectDetails(project);
        System.out.println("Project updated: " + dbProject);
        try {
            currentProject = project;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void selectProject() {
        listProjects();
        Integer projectId = getIntInput("Enter a project ID to select a project");
        currentProject = projectService.fetchProjectById(projectId);
    }

    private void listProjects() {
        List<Project> projects = projectService.fetchAllProjects();
        System.out.println("\nProjects: ");
        projects.forEach(project -> System.out.println(project.getProjectId() + ": " + project.getProjectName()));
    }

    private void printOperations() {
        System.out.println("\nThese are the available selections. Press the Enter key to quit: ");
        operations.forEach(line -> System.out.println(" " + line));
        if (Objects.isNull(currentProject)) {
            System.out.println("\nYou are not working with a project at the moment.");
        } else {
            System.out.println("\nYou are working with project: " + projectService.fetchProjectById(currentProject.getProjectId()));
        }
    }

    public static void main(String[] args) {
        new ProjectsApp().processUserSelections();
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu selection: ");
        return Objects.isNull(input) ? -1 : input; // if input is null, return -1 otherwise return input.
    }

    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(prompt)) {
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. \nPlease try again.");
        }

    }

    private String getStringInput(String prompt) {
        System.out.println(prompt + ": ");
        String input = scanner.nextLine();
        return input.isBlank() ? null : input.trim();
    }

    public void createProject() {
        String projectName = getStringInput("Enter the project name");
        String estimatedHours = getStringInput("Enter the estimated hours");
        BigDecimal estimatedHoursDecimal = Objects.isNull(estimatedHours) ? BigDecimal.valueOf(0.00) : new BigDecimal(estimatedHours);
        String actualHours = getStringInput("Enter the actual hours");
        BigDecimal actualHoursDecimal = Objects.isNull(estimatedHours) ? BigDecimal.valueOf(0.00) : new BigDecimal(actualHours);
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHoursDecimal);
        project.setActualHours(actualHoursDecimal);
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        Project dbProject = projectService.addProject(project);
        System.out.println("Project added: " + dbProject);
    }

    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return new BigDecimal(input).setScale(2, 2);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. \nPlease try again.");
        }
    }

    private boolean exitMenu() {
        System.out.println("Goodbye!");
        return true;
    }
}
