import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat

import groovy.time.TimeCategory

public class Projector {
  static class Investor {
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy")
    DecimalFormat df = new DecimalFormat('0,000.00', DecimalFormatSymbols.getInstance(Locale.getDefault()))

    String name
    Date start = new Date()
    Date current = new Date()
    Date birthday
    Float initialBalance, balance, initialSalary, salary, salaryGrowth

    Investor(name, birthday, initialBalance, initialSalary) {
      this.name = name;
      this.birthday = birthday;
      this.initialBalance = initialBalance;
      this.initialSalary = initialSalary;

      balance = initialBalance;
      current = start;
      salary = initialSalary;
    }

    void contribute(years, employeeContribution, employerContriubution, interestRate, salaryGrowth) {
      Float employeeContrib, employerContrib
      for(int i = 1; i <= years; i++) {
        Float interest = this.balance * interestRate;
        employeeContrib = salary * employeeContribution
        employerContrib = salary * employerContriubution
        balance = balance + interest + employeeContrib + employerContrib
        use(TimeCategory) {
          current = current + years.year
        }
        salary = salary * (1 + salaryGrowth);
      }

      display(employeeContrib, employerContrib);
    }

    void accumulate(years, interestRate) {
      for(int i = 1; i <= years; i++) {
        Float interest = this.balance * interestRate;
        balance = balance + interest;

        use(TimeCategory) {
          current = current + years.year
        }
      }

      groovy.time.TimeDuration age
      use(TimeCategory) {
        age = current - birthday;
      }

      println "${sdf.format(current)} (${name} - age ${age.days / 365 as Integer}): \$ ${df.format(balance)}"
    }

    void display(employeeContrib, employerContrib) {
      groovy.time.TimeDuration age
      use(TimeCategory) {
        age = current - birthday;
      }
      println "${sdf.format(current)} (${name} - age ${age.days / 365 as Integer}, \$ ${df.format(salary)} / yr, \$ ${df.format(employeeContrib)} employee, \$ ${df.format(employerContrib)} employer): \$ ${df.format(balance)}"
    }
  }

  public static void main(String [] args) {
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy")
    DecimalFormat df = new DecimalFormat('0,000.00', DecimalFormatSymbols.getInstance(Locale.getDefault()))

    Investor josh = new Investor(
      "Josh", Date.parse("yyyy-MM-dd", "1990-01-01"), 100000.0f, 100000.0f)

    Investor annie = new Investor(
      "Annie", Date.parse("yyyy-MM-dd", "1990-01-01"), 100000.08f, 100000.0f)

    for(int i = 1; i <= 15; i++) {
      josh.contribute(1, 0.05, 0.04, 0.07, 0.00);
    }

    for(int i = 1; i <= 15; i++) {
      annie.contribute(1, 0.06, 0.03, 0.07, 0.00);
    }

    String total = df.format(josh.balance + annie.balance)
    println "Total: \$${total}"

    for(int i = 1; i <= 10; i++) {
      annie.accumulate(1, 0.07)
      josh.accumulate(1, 0.07)
    }

    total = df.format(josh.balance + annie.balance)
    println "Total: \$${total}"

  }
}