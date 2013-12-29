<<<<<<< HEAD
﻿bayes_classer
==============
Write-up link:
https://docs.google.com/document/d/1u-OqvLkhMfWNwOsg_DFaG7AihSjYatMWXZ_h9FR9WPU/edit?

Assignment link:
http://www.cs.illinois.edu/~slazebni/fall13/assignment3.html

usp=sharing
=======
Digit-and-Face-Classification
=============================

Naive Bayes Classification

Group Members: Erik Muro, William Hempy

Assignment link: http://www.cs.illinois.edu/~slazebni/fall13/assignment3.html
>>>>>>> 6811e0dd083a82a927f85caa492295a6c0fba1d7

Contents

    Part 1.1 (for everybody): Digit classification
    Part 1.2 (for everybody): Face classification
    Part 2 (for four-unit students): Text document classification
    Submission instructions 

Part 1 (For everybody): Digit classification


(Adapted from Berkeley CS 188 project 5)

    Data: This file is a zip archive containing training and test digits, together with their ground truth labels (see readme.txt in the zip archive for an explanation of the data format). There are 5000 training exemplars (roughly 500 per class) and 1000 test exemplars (roughly 100 per class).

    Features: The basic feature set consists of a single binary indicator feature for each pixel. Specifically, the feature Fij indicates the status of the (i,j)th pixel. Its value is 1 if the pixel is foreground (no need to distinguish between the two different foreground values), and 0 if it is background.

    Training: The goal of the training stage is to estimate the likelihoods P(Fij | class) for every pixel location (i,j) and for every digit class from 0 to 9. The likelihood estimate is defined as

    P(Fij = f | class) = (# of times pixel (i,j) has value f in training examples from this class) / (Total # of training examples from this class).

    In addition, as discussed in the lecture, you have to smooth the likelihoods to ensure that there are no zero counts. Laplace smoothing is a very simple method that increases the observation count of every value f by some constant k. This corresponds to adding k to the numerator above, and k*V to the denominator (where V is the number of possible values the feature can take on). The higher the value of k, the stronger the smoothing. Experiment with different integer values of k (say, from 1 to 50) and find the one that gives the highest classification accuracy.

    You should also estimate the priors P(class) by the empirical frequencies of different classes in the training set.

    Testing: You will perform maximum a posteriori (MAP) classification of test digits according to the learned Naive Bayes model. Suppose a test image has feature values f1,1, f1,2, ... , f28,28. According to this model, the posterior probability (up to scale) of each class given the digit is given by

    P(class) ⋅ P(f1,1|class) ⋅ P(f1,2|class) ⋅ ...⋅ P(f28,28 | class).

    Note that in order to avoid underflow, you need to work with the log of the above quantity:

    log P(class) + log P(f1,1|class) + log P(f1,2|class) + ... + log P(f28,28 | class).

    After you compute the above decision function values for all ten classes for every test image, you will use them for MAP classification. You should also try maximum likelihood (ML) classification omitting the prior term from the above, and report whether this makes a lot of difference in the results.

    Evaluation: Use the true class labels of the test images from the testlabels file to check the correctness of the estimated label for each test digit. Report your performance in terms of the classification rate for each digit (percentage of all test images of a given digit correctly classified). Also report your confusion matrix. This is a 10x10 matrix whose entry in row r and column c is the percentage of test images from class r that are classified as class c. In addition, for each digit class, show the test example with the highest posterior probability (i.e., the most "prototypical" instance of that digit), as well as some "interesting" examples of incorrect classifications made by your system.

    Important: The ground truth labels of test images should be used only to evaluate classification accuracy. They should not be used in any way during the decision process.

    Tip: You should be able to achieve at least 70% accuracy on the test set. One "warning sign" that you have a bug in your implementation is if some digit gets 100% or 0% classification accuracy (that is, your system either labels all the test images as the same class, or never wants to label any test images as some particular class).

    Odds ratios: When using classifiers in real domains, it is important to be able to inspect what they have learned. One way to inspect a naive Bayes model is to look at the most likely features for a given label. Another tool for understanding the parameters is to look at odds ratios. For each pixel feature Fij and pair of classes c1, c2, the odds ratio is defined as

    odds(Fij=1, c1, c2) = P(Fij=1 | c1) / P(Fij=1 | c2).

    This ratio will be greater than one for features which cause belief in c1 to increase over the belief in c2. The features that have the greatest impact on classification are those with both a high probability (because they appear often in the data) and a high odds ratio (because they strongly bias one label versus another).

    Take four pairs of digits that have the highest confusion rates according to your confusion matrix, and for each pair, display the maps of feature likelihoods for both classes as well as the odds ratio for the two classes. For example, the figure below shows the log likelihood maps for 1 (left), 8 (center), and the log odds ratio for 1 over 8 (right):



    If you cannot do a graphical display like the one above, you can display the maps in ASCII format using some coding scheme of your choice. For example, for the odds ratio map, you can use '+' to denote features with positive log odds, ' ' for features with log odds close to 1, and '-' for features with negative log odds. 

Part 1.2 (for everybody): Face classification
(Adapted from Berkeley CS 188 project 5)

The second task is classifying windows cropped from images as containing a face or not. This is a basic step in face detection, where a window is scanned over the image, and for each window location, the classifier has to answer the question of whether a face is present. So, unlike the digit classification problem above, here the decision problem is binary.

    This data file, in a similar format to that of the digit data, contains training and test patches and binary labels, where 0 corresponds to 'non-face' and 1 corresponds to 'face'. The patches themselves are higher-resolution than the digit images, and each pixel value is either '#', corresponding to an edge being found at that location, or ' ', corresponding to a non-edge pixel.

    As in Part 1.1, train a Naive Bayes classifier to tell faces from non-faces. In your report, describe any interesting experimental settings (smoothing constant, etc.), and give the confusion matrix for this task, which should have four entries. Also give the odds ratio image for faces vs. non-faces and show a few interesting examples of false positives (non-faces classified as faces) and false negatives (faces classified as non-faces). 

Part 1 Extra Credit

    Experiment with more sophisticated features to improve the accuracy of the Naive Bayes model. In particular, consider non-binary features or features that combine the values of several pixels. You can also try to implement a bag of features model (see this lecture from my computer vision class for details). 
