# simplexity

Wrapping and extending the Stanford Javaplex library for computing homology of spaces.

## Computing what now?

Roughly speaking, the homology of a space is map from dimensions 0, 1, 2, ... to the count of the number of 'holes' of that dimension the space contains. The 0th number counts the number of connected components in the space. The 1st number counts the number of loops the space contains. The 2nd number counts the number of bubbles the space contains. The 3rd number... well, we don't have a word for that, but it's the same idea --- there is a region of that dimension which is completely enclosed, yet not filled.

There is some very interesting work on 'persistence homology,' in which a point cloud is made into a sequence of simplicial complexes, and the homology is computing for each of them. The idea is that, if a particular hole is persistent over a long interval, it might be meaningful rather than noise. 

My hypothesis is that if you turn a discrete data set into a sequence of simplicial complexes using a more abstract distance metric (say, "how many of these predicates are true") that persistent holes will be similarly informative --- or at least, will indicate places of potential interest to interactively explore. 

## Status

I'm still working out how Javaplex works, and coming up with a sensible API for this library. Currently you can make simplices with integer vertices. Next up is working with simplicial complexes of integer vertices. Then, I'll want to figure out a way to have arbitrary data as the vertices in a complex, and a strategy for working with simplices like they're regular values: operations to combine two complexes, subtract simplices from them, etc. At that point I should have a sense of whether this approach is performant enough to be useful, or if it would be best to implement the homology computations from scratch using matrix methods. 

## Contributing

I understand the math of this library okay, but I am new to working with Java from Clojure. Any advice on how to spec a Java library, and how to 'immutabilize' imperative-style data structures in Clojure wrapping, is most welcome.
