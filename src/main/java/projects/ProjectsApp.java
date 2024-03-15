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
    private ProjectService projectService = new ProjectService();
    private Project currentProject;
    private final List<String> operations = List.of(
            "1) Add a project",
            "2) Fetch all projects",
            "3) Select a project");

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
                    default:
                        System.out.println("Invalid selection. Please try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again." + e.getMessage());
            }

        }
    }

    private void selectProject() {
        listProjects();
        Integer projectId = getIntInput("Enter a project ID to selecet a project");
        currentProject = null;
        currentProject = projectService.fetchProjectById(projectId);
        if (Objects.isNull(currentProject)) {
            System.out.println("Invalid project ID selected.");
        }
    }

    private void listProjects() {
        currentProject = null;
        List<Project> projects = projectService.fetchAllProjects();
        System.out.println("\nProjects: ");
        projects.forEach(project -> System.out.println(project.getProjectId() + ": " + project.getProjectName()));
    }

    private void printOperations() {
        System.out.println("\nThese are the available selections. Press the Enter key to quit: ");
        operations.forEach(line -> System.out.println(" " + line));
        if (Objects.isNull(currentProject)) {
            System.out.println("\nYou are not working with a project.");
        } else {
            System.out.println("\nYou are working with project: " + currentProject);
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
        if (Objects.isNull(input)) {
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
        BigDecimal estimatedHoursDecimal = getDecimalInput(estimatedHours);
        String actualHours = getStringInput("Enter the actual hours");
        BigDecimal actualHoursDecimal = getDecimalInput(actualHours);
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
