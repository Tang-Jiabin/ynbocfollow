package com.zykj.follow.common;

import java.util.*;

/**
 * @author J.Tang
 * @version V1.0
 * @email seven_tjb@163.com
 * @date 2020-11-29
 */

public final class AliasMethod {


    private final Random random;

    private final int[] alias;
    private final double[] probability;


    public AliasMethod(List<Double> probabilities) {
        this(probabilities, new Random());
    }

    public AliasMethod(List<Double> probabilities, Random random) {
        if (probabilities == null || random == null) {
            throw new NullPointerException();
        }
        if (probabilities.size() == 0) {
            throw new IllegalArgumentException("Probability vector must be nonempty.");
        }

        probability = new double[probabilities.size()];
        alias = new int[probabilities.size()];

        this.random = random;

        final double average = 1.0 / probabilities.size();

        probabilities = new ArrayList<Double>(probabilities);

        Deque<Integer> small = new ArrayDeque<Integer>();
        Deque<Integer> large = new ArrayDeque<Integer>();

        for (int i = 0; i < probabilities.size(); ++i) {
            if (probabilities.get(i) >= average) {
                large.add(i);
            } else {
                small.add(i);
            }
        }


        while (!small.isEmpty() && !large.isEmpty()) {

            int less = small.removeLast();
            int more = large.removeLast();


            probability[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;


            probabilities.set(more,
                    (probabilities.get(more) + probabilities.get(less)) - average);


            if (probabilities.get(more) >= 1.0 / probabilities.size()) {
                large.add(more);
            } else {
                small.add(more);
            }
        }


        while (!small.isEmpty()) {
            probability[small.removeLast()] = 1.0;
        }
        while (!large.isEmpty()) {
            probability[large.removeLast()] = 1.0;
        }
    }


    public int next() {
        int column = random.nextInt(probability.length);

        boolean coinToss = random.nextDouble() < probability[column];

        return coinToss ? column : alias[column];
    }

}