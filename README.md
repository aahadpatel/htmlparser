>**HTML Parser:**
Calculates the score for a given HTML file and save the score, as well as the date and time it was calculated. It can retrieve all scores generated in a provided date range, retrieve the all time highest score for a given file, the all time lowest score for a given file, and to see the average score for a given file.

* Score HTML content using the provided scoring guide
* Saved results to a mySQL
* A user should be able to retrieve all scores for a given file
* A user should be able to retrieve all scores run in the system for a custom date range
* A user should be able to retrieve highest score for a given file
* A user should be able to retrieve lowest score for a given file
* A user should be able to retrieve the average score for each unique file name

>**Languages and tools used:**
>
>- Node v8.4.0
>- NPM v5.3.0
>- MySQL 5.7.17
>- Eclipse as my IDE
>- macOS High Sierra

Scoring Rules
-------------
Each starting tag in the table below has been assigned a score. Any tags not listed in this table will not factor into scoring. Each tag in the content should be added to or subtracted from the total score based on this criteria.

| TagName | Score Modifier | TagName | Score Modifier |
| ------- | :------------: | ------- | -------------- |
| div     | 3              | font    | -1             |
| p       | 1              | center  | -2             |
| h1      | 3              | big     | -2             |
| h2      | 2              | strike  | -1             |
| html    | 5              | tt      | -2             |
| body    | 5              | frameset| -5             |
| header  | 10             | frame   | -5             |
| footer  | 10             |

