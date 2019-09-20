### Demonstration Video: https://youtu.be/2Q9dLzOwM0Y

### Download: https://ifcommissions.noahkurrack.com/jars/
### Download sample contracts: https://ifcommissions.noahkurrack.com/contracts/

## About

### Summary

The Invisible Fence Commissions Calculator is a Java Swing application designed to streamline the process of calculating commissions for employee-sold contracts. The cost of each contract is calculated using a database (json file) of parts and their associated costs. The profit of the contract (subtotal - cost) is used to determine the ratio of profit to cost and percentage of the profit that the employee is awarded for the sale. The application loads the contracts from excel files exported from the Invisible Fence CRM. The aplication also creates detailed reports for each employee that include each contract's determined parts and all calculations (for manual reveiw).

I wrote this application from scratch with the help of the [Java Swing Documentation](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html) and IntelliJ GUI Forms. Unless otherwise noted (couple of utility classes in Util package), all code is my own.

### Interface Structure

- Setup: basic parameters are entered
- Configuration: manipulation of item costs in contracts
- Settings:  manual manipulation of calculation parameters
- Run: displays summary of output

### Instructions

Simply execute the jar file after installing Java 8. 

OR use the IFCommissions Launcher [here](https://github.com/NoahTK7/IFCommissionsLauncher) to receive automatic updates each time the application is launched.

For all downloads, visit https://ifcommissions.noahkurrack.com/jars/ .

### Credit

Copyright (C) 2018 Noah Kurrack. All Rights Reserved.

Contents of this project may be distributed or copied for non-commercial use only.

#### Open Source Libraries
The following third party libraries that are used in the project are licensed under the Apache License, Version 2.0; software used in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
- json-simple version 1.1.1 (http://code.google.com/p/json-simple/)
- Apache POI version 3.17 (https://poi.apache.org/)