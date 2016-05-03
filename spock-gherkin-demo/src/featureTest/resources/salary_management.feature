Feature: Salary Management

    Scenario Outline: Modify all employees salary
        Given the salary management system is initialized with the following data
            | id  | user    | salary  |
            | 1   | donald  | 60000.0 |
            | 2   | dewie   | 62000.0 |
            | 3   | goofy   | 55000.0 |
            | 4   | scrooge | 70000.0 |
            | 5   | daisy   | 56000.0 |
            | 6   | minnie  | 62000.0 |
            | 7   | mickey  | 51000.0 |
            | 8   | fethry  | 66500.0 |
        When the boss increases the salary for the employee with id '<id>' by <percent>%
        Then the payroll for the employee with id '<id>' should display a salary of <expected>
        Examples:
            | id  | percent | expected  |
            | 1   | 2       | 61200     |
            | 2   | 7       | 66340     |
            | 3   | 5       | 57750     |
            | 4   | 12      | 78400     |
            | 5   | -2      | 54880     |
            | 6   | 4       | 64480     |
            | 7   | -7      | 47430     |
            | 8   | 0       | 66500     |
