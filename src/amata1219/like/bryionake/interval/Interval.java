package amata1219.like.bryionake.interval;

public class Interval<N extends Number & Comparable<N>> {

    private final Endpoint<N> lowerEndpoint, upperEndpoint;

    public Interval(Endpoint<N> lowerEndpoint, Endpoint<N> upperEndpoint) {
        this.lowerEndpoint = lowerEndpoint;
        this.upperEndpoint = upperEndpoint;
    }

    public boolean contains(N value) {
        return lowerEndpoint.hasBoundaryValueBelow(value) && upperEndpoint.hasBoundaryValueAbove(value);
    }

}
