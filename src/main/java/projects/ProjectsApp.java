package projects;

import entity.Project;
import projects.exceptions.DbException;
import service.ProjectService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ProjectsApp {

    private final ProjectService projectService = new ProjectService();
    private final List<String> operations = List.of("1) Add a project");
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ProjectsApp projectsApp = new ProjectsApp();
        projectsApp.processUserSelections();
    }

    private void processUserSelections() {
        boolean done = false;
        while (!done) {
            int selection = 0;
            try {
                selection = getUserSelection();
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
            }
            switch (selection) {
                case -1:
                    done = exitMenu();
                    break;
                case 1:
                    createProject();
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.");
                    done = true;
            }
        }
    }

    private boolean exitMenu() {
        System.out.println("Goodbye!");
        return true;
    }

    private int getUserSelection() {
        printOperations();
        Integer input = getIntInput("Enter a menu selection: ");
        return Objects.isNull(input) ? -1 : input; // if input is null, return -1 otherwise return input.
    }

    private Integer getIntInput(String prompt) {
        String input = null;
        if (Objects.isNull(input)) {
            return null;
        }
        try {
            input = getStringInput(prompt);
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            throw new DbException(input + " is not a valid number. \nPlease try again.");
        }

    }

    private String getStringInput(String prompt) {
        System.out.println(prompt + ": ");
        String input = scanner.nextLine();
        return input.isBlank() ? null : input;
    }

    private void printOperations() {
        System.out.println("\nThese are the available selections. Press the Enter key to quit: ");
        operations.forEach(System.out::println);
    }

    public void createProject() {
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        Integer actualHours = getIntInput(   "Enter the actual hours");
        Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
        String notes = getStringInput("Enter the project notes");
        Project project = new Project();
        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(BigDecimal.valueOf(actualHours));
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        Project dbProject = projectService.addProject(project);
        System.out.println("Project added: " + dbProject);
    }

    private BigDecimal getDecimalInput(String input) {
        return new BigDecimal(input).setScale(2);
    }
}
