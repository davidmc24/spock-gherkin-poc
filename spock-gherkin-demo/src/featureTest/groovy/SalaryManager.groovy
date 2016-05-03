class SalaryManager {
	Map<Integer, Employee> employees = [:]

	SalaryManager(List<Employee> employees) {
		this.employees = employees.collectEntries { [it.id, it] }
	}

    void increaseSalary(Integer id, int increaseInPercent) {
		def nominee = employees[id]
		def oldSalary = nominee.salary
        nominee.salary = oldSalary + oldSalary * increaseInPercent / 100
	}

    Employee getPayroll(int id) {
		return employees.get(id)
	}
}
