# simple tree
## Overview
simple tree is a simple/demonstrative implementation of <b>decision trees</b>

This implementation is more a demo than rather production ready, take this as it is.

A decision tree is a decision support tool that uses a tree-like models of decisions and their possible consequences, including chance event outcomes, resource costs, and utility.

## Usage

In order to use simple tree you must create a class that represent the element you want to calculate the outcome for and make this class implement the interface <b>Element</b>.
And the field that we want to get the outcome for must be marked with the <b>ClassField</b> annotation.
In the current version these implementations of <b>Element</b> must have all their fields as String, this will be analyzed and eventually changed in latter versions

An example of a <b>Element</b> implementation is the <b>Transport</b> class from the test package.

```javascript
DecisionTree<Transport> decider = new DecisionTreeImpl<>();
decider.learn(TestUtil.readFromResource("transport.txt"));
Transport t = new Transport ();
//TODO set the fields to the Transport instance
Optional<String> outcome = decider.outcome(e);
```

This basic example shows a simple usage for a decision tree.

## Theoretical overview

In the learning phase, we plan to create a tree that we yields the best prediction for the class field (the field is of interest to us and that we will try to latter predict).
This being a supervised learning first we need a set of data the also has the missing field.

With this data we look at the fields that are not the class field determine which of them will provided us with the best info gain.
To determine the info gain we will use the [Gini index](https://en.wikipedia.org/wiki/Gini_coefficient). After this we determine if all the fields are "pure class",
if not we continue with the splitting recursively.

Please look at the provided comments/java docs for a better picture and check out the external resources.

## Further reading/external resources

You can read more about the subject [here](http://people.revoledu.com/kardi/tutorial/DecisionTree/what-is-decision-tree.htm)

