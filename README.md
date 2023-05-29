# COMP 3021 Programming Assignment 1 (PA1) (Spring 2023)

## Grading Policy

We explain the grading policy before task specification so that you will not miss it.

| **Item**                                        | **Ratio** | **Notes**                                                                |
|-------------------------------------------------|-----------|--------------------------------------------------------------------------|
| Keeping your GitHub repository private          | 5%        | You must keep your repository **private** at all times.                  |
| Having at least three commits on different days | 5%        | You should commit three times during different days in your repository.  |
| Code style                                      | 10%       | You get 10% by default, and every 5 warnings from CheckStyle deducts 1%. |                     |
| Public test cases                               | 30%       | (# of passing tests / # of provided tests) * 20%                         |
| Hidden test cases                               | 50%       | (# of passing tests / # of provided tests) * 50%                         |

Please try to compile your code with `./gradlew build` before submission.
You will not get any marks of public/hidden test
cases if your code does not compile.


## Submission

You should submit a single text file specified as follows:

- A file named `<itsc-id>.txt` containing the URL of your private repository at the first line. We will ask you to add
  the TAs' accounts as collaborators near the deadline.

For example, a student CHAN, Tai Man with ITSC ID `tmchanaa` having a repository
at `https://github.com/tai-man-chan/COMP3021-PA1` should submit a file named `tmchanaa.txt` with the following content:

```text
https://github.com/tai-man-chan/COMP3021-PA1
```

Note that we are using automatic scripts to process your submission.
**DO NOT add extra explanation** to the file; otherwise they will prevent our scripts from correctly processing your
submission.
Feel free to email us if you need clarification.

You need to submit the file to Canvas. The deadline for this assignment is **March 12, 2023, 23:59:59**.

**We will grade your submission based on the latest committed version before the deadline.**
Please make sure all the amendments are made before the deadline and do not make changes after the deadline.

## Run Check Style

We have pre-configured a gradle task to check style for you.
You can run `./gradlew checkstyleMain` in the integrated terminal of IntelliJ to check style.

## Academic Integrity

We trust that you are familiar with Honor Code of HKUST. If not, refer to
[this page](https://course.cse.ust.hk/comp3021/#honorcode).

