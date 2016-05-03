import spockgherkin.core.FeatureFilename
import spock.lang.Specification
import spock.lang.Title

@Title("Salary Management")
@FeatureFilename("salary_management.feature")
class SalarySpec extends Specification {
    def "Modify all employees salary" () {
        given: """the salary management system is initialized with the following data"""
        def data = [
            [1, 'donald', 60000.0],
            [2, 'dewie', 62000.0],
            [3, 'goofy', 55000.0],
            [4, 'scrooge', 70000.0],
            [5, 'daisy', 56000.0],
            [6, 'minnie', 62000.0],
            [7, 'mickey', 51000.0],
            [8, 'fethry', 66500.0]
        ]
        def manager = new SalaryManager(employees(data))

        when: "the boss increases the salary for the employee with id '<id>' by <percent>%"
        manager.increaseSalary(id, percent)

        then: "the payroll for the employee with id '<id>' should display a salary of <expected>"
        def nominee = manager.getPayroll(id)
        nominee.salary == expected

        where:
        id  | percent || expected
        1   |   2     || 61200.0f
        2   |   7     || 66340.0f
        3   |   5     || 57750.0f
        4   |   12    || 78400.0f
        5   |   -2    || 54880.0f
        6   |   4     || 64480.0f
        7   |   -7    || 47430.0f
        8   |   0     || 66500.0f
    }

    def employees(data) {
        data.collect { new Employee(id: it[0], user: it[1], salary: it[2])}
    }
}
