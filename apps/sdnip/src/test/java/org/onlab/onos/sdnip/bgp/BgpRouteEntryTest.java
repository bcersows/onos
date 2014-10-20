package org.onlab.onos.sdnip.bgp;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.onlab.packet.IpAddress;
import org.onlab.packet.IpPrefix;

/**
 * Unit tests for the BgpRouteEntry class.
 */
public class BgpRouteEntryTest {
    private BgpSession bgpSession;
    private static final IpAddress BGP_SESSION_BGP_ID =
        IpAddress.valueOf("10.0.0.1");
    private static final IpAddress BGP_SESSION_IP_ADDRESS =
        IpAddress.valueOf("20.0.0.1");

    private BgpSession bgpSession2;
    private static final IpAddress BGP_SESSION_BGP_ID2 =
        IpAddress.valueOf("10.0.0.2");
    private static final IpAddress BGP_SESSION_IP_ADDRESS2 =
        IpAddress.valueOf("20.0.0.1");

    private BgpSession bgpSession3;
    private static final IpAddress BGP_SESSION_BGP_ID3 =
        IpAddress.valueOf("10.0.0.1");
    private static final IpAddress BGP_SESSION_IP_ADDRESS3 =
        IpAddress.valueOf("20.0.0.2");

    @Before
    public void setUp() throws Exception {
        // Mock objects for testing
        bgpSession = createMock(BgpSession.class);
        bgpSession2 = createMock(BgpSession.class);
        bgpSession3 = createMock(BgpSession.class);

        // Setup the BGP Sessions
        expect(bgpSession.getRemoteBgpId())
            .andReturn(BGP_SESSION_BGP_ID).anyTimes();
        expect(bgpSession.getRemoteIp4Address())
            .andReturn(BGP_SESSION_IP_ADDRESS).anyTimes();
        //
        expect(bgpSession2.getRemoteBgpId())
            .andReturn(BGP_SESSION_BGP_ID2).anyTimes();
        expect(bgpSession2.getRemoteIp4Address())
            .andReturn(BGP_SESSION_IP_ADDRESS2).anyTimes();
        //
        expect(bgpSession3.getRemoteBgpId())
            .andReturn(BGP_SESSION_BGP_ID3).anyTimes();
        expect(bgpSession3.getRemoteIp4Address())
            .andReturn(BGP_SESSION_IP_ADDRESS3).anyTimes();

        replay(bgpSession);
        replay(bgpSession2);
        replay(bgpSession3);
    }

    /**
     * Generates a BGP Route Entry.
     *
     * @return a generated BGP Route Entry
     */
    private BgpRouteEntry generateBgpRouteEntry() {
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        byte pathSegmentType1 = (byte) BgpConstants.Update.AsPath.AS_SEQUENCE;
        ArrayList<Long> segmentAsNumbers1 = new ArrayList<>();
        segmentAsNumbers1.add((long) 1);
        segmentAsNumbers1.add((long) 2);
        segmentAsNumbers1.add((long) 3);
        BgpRouteEntry.PathSegment pathSegment1 =
            new BgpRouteEntry.PathSegment(pathSegmentType1, segmentAsNumbers1);
        pathSegments.add(pathSegment1);
        //
        byte pathSegmentType2 = (byte) BgpConstants.Update.AsPath.AS_SET;
        ArrayList<Long> segmentAsNumbers2 = new ArrayList<>();
        segmentAsNumbers2.add((long) 4);
        segmentAsNumbers2.add((long) 5);
        segmentAsNumbers2.add((long) 6);
        BgpRouteEntry.PathSegment pathSegment2 =
            new BgpRouteEntry.PathSegment(pathSegmentType2, segmentAsNumbers2);
        pathSegments.add(pathSegment2);
        //
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 100;
        long multiExitDisc = 20;

        BgpRouteEntry bgpRouteEntry =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry.setMultiExitDisc(multiExitDisc);

        return bgpRouteEntry;
    }

    /**
     * Tests valid class constructor.
     */
    @Test
    public void testConstructor() {
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();

        String expectedString =
            "BgpRouteEntry{prefix=1.2.3.0/24, nextHop=5.6.7.8, " +
            "bgpId=10.0.0.1, origin=0, asPath=AsPath{pathSegments=" +
            "[PathSegment{type=2, segmentAsNumbers=[1, 2, 3]}, " +
            "PathSegment{type=1, segmentAsNumbers=[4, 5, 6]}]}, " +
            "localPref=100, multiExitDisc=20}";
        assertThat(bgpRouteEntry.toString(), is(expectedString));
    }

    /**
     * Tests invalid class constructor for null BGP Session.
     */
    @Test(expected = NullPointerException.class)
    public void testInvalidConstructorNullBgpSession() {
        BgpSession bgpSessionNull = null;
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 100;

        new BgpRouteEntry(bgpSessionNull, prefix, nextHop, origin, asPath,
                    localPref);
    }

    /**
     * Tests invalid class constructor for null AS Path.
     */
    @Test(expected = NullPointerException.class)
    public void testInvalidConstructorNullAsPath() {
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        BgpRouteEntry.AsPath asPath = null;
        long localPref = 100;

        new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                    localPref);
    }

    /**
     * Tests getting the fields of a BGP route entry.
     */
    @Test
    public void testGetFields() {
        // Create the fields to compare against
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        byte pathSegmentType1 = (byte) BgpConstants.Update.AsPath.AS_SEQUENCE;
        ArrayList<Long> segmentAsNumbers1 = new ArrayList<>();
        segmentAsNumbers1.add((long) 1);
        segmentAsNumbers1.add((long) 2);
        segmentAsNumbers1.add((long) 3);
        BgpRouteEntry.PathSegment pathSegment1 =
            new BgpRouteEntry.PathSegment(pathSegmentType1, segmentAsNumbers1);
        pathSegments.add(pathSegment1);
        //
        byte pathSegmentType2 = (byte) BgpConstants.Update.AsPath.AS_SET;
        ArrayList<Long> segmentAsNumbers2 = new ArrayList<>();
        segmentAsNumbers2.add((long) 4);
        segmentAsNumbers2.add((long) 5);
        segmentAsNumbers2.add((long) 6);
        BgpRouteEntry.PathSegment pathSegment2 =
            new BgpRouteEntry.PathSegment(pathSegmentType2, segmentAsNumbers2);
        pathSegments.add(pathSegment2);
        //
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 100;
        long multiExitDisc = 20;

        // Generate the entry to test
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();

        assertThat(bgpRouteEntry.prefix(), is(prefix));
        assertThat(bgpRouteEntry.nextHop(), is(nextHop));
        assertThat(bgpRouteEntry.getBgpSession(), is(bgpSession));
        assertThat(bgpRouteEntry.getOrigin(), is(origin));
        assertThat(bgpRouteEntry.getAsPath(), is(asPath));
        assertThat(bgpRouteEntry.getLocalPref(), is(localPref));
        assertThat(bgpRouteEntry.getMultiExitDisc(), is(multiExitDisc));
    }

    /**
     * Tests whether a BGP route entry is a local route.
     */
    @Test
    public void testIsLocalRoute() {
        //
        // Test non-local route
        //
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();
        assertThat(bgpRouteEntry.isLocalRoute(), is(false));

        //
        // Test local route with AS Path that begins with AS_SET
        //
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        byte pathSegmentType1 = (byte) BgpConstants.Update.AsPath.AS_SET;
        ArrayList<Long> segmentAsNumbers1 = new ArrayList<>();
        segmentAsNumbers1.add((long) 1);
        segmentAsNumbers1.add((long) 2);
        segmentAsNumbers1.add((long) 3);
        BgpRouteEntry.PathSegment pathSegment1 =
            new BgpRouteEntry.PathSegment(pathSegmentType1, segmentAsNumbers1);
        pathSegments.add(pathSegment1);
        //
        byte pathSegmentType2 = (byte) BgpConstants.Update.AsPath.AS_SEQUENCE;
        ArrayList<Long> segmentAsNumbers2 = new ArrayList<>();
        segmentAsNumbers2.add((long) 4);
        segmentAsNumbers2.add((long) 5);
        segmentAsNumbers2.add((long) 6);
        BgpRouteEntry.PathSegment pathSegment2 =
            new BgpRouteEntry.PathSegment(pathSegmentType2, segmentAsNumbers2);
        pathSegments.add(pathSegment2);
        //
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 100;
        long multiExitDisc = 20;
        //
        bgpRouteEntry =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry.setMultiExitDisc(multiExitDisc);
        assertThat(bgpRouteEntry.isLocalRoute(), is(true));

        //
        // Test local route with empty AS Path
        //
        pathSegments = new ArrayList<>();
        asPath = new BgpRouteEntry.AsPath(pathSegments);
        bgpRouteEntry =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry.setMultiExitDisc(multiExitDisc);
        assertThat(bgpRouteEntry.isLocalRoute(), is(true));
    }

    /**
     * Tests getting the BGP Neighbor AS number for a route.
     */
    @Test
    public void testGetNeighborAs() {
        //
        // Get neighbor AS for non-local route
        //
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();
        assertThat(bgpRouteEntry.getNeighborAs(), is((long) 1));

        //
        // Get neighbor AS for a local route
        //
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 100;
        long multiExitDisc = 20;
        //
        bgpRouteEntry =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry.setMultiExitDisc(multiExitDisc);
        assertThat(bgpRouteEntry.getNeighborAs(), is(BgpConstants.BGP_AS_0));
    }

    /**
     * Tests whether a BGP route entry has AS Path loop.
     */
    @Test
    public void testHasAsPathLoop() {
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();

        // Test for loops: test each AS number in the interval [1, 6]
        for (int i = 1; i <= 6; i++) {
            assertThat(bgpRouteEntry.hasAsPathLoop(i), is(true));
        }

        // Test for non-loops
        assertThat(bgpRouteEntry.hasAsPathLoop(500), is(false));
    }

    /**
     * Tests the BGP Decision Process comparison of BGP routes.
     */
    @Test
    public void testBgpDecisionProcessComparison() {
        BgpRouteEntry bgpRouteEntry1 = generateBgpRouteEntry();
        BgpRouteEntry bgpRouteEntry2 = generateBgpRouteEntry();

        //
        // Compare two routes that are same
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(true));

        //
        // Compare two routes with different LOCAL_PREF
        //
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        byte pathSegmentType1 = (byte) BgpConstants.Update.AsPath.AS_SEQUENCE;
        ArrayList<Long> segmentAsNumbers1 = new ArrayList<>();
        segmentAsNumbers1.add((long) 1);
        segmentAsNumbers1.add((long) 2);
        segmentAsNumbers1.add((long) 3);
        BgpRouteEntry.PathSegment pathSegment1 =
            new BgpRouteEntry.PathSegment(pathSegmentType1, segmentAsNumbers1);
        pathSegments.add(pathSegment1);
        //
        byte pathSegmentType2 = (byte) BgpConstants.Update.AsPath.AS_SET;
        ArrayList<Long> segmentAsNumbers2 = new ArrayList<>();
        segmentAsNumbers2.add((long) 4);
        segmentAsNumbers2.add((long) 5);
        segmentAsNumbers2.add((long) 6);
        BgpRouteEntry.PathSegment pathSegment2 =
            new BgpRouteEntry.PathSegment(pathSegmentType2, segmentAsNumbers2);
        pathSegments.add(pathSegment2);
        //
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 50;                                    // Different
        long multiExitDisc = 20;
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(false));
        localPref = bgpRouteEntry1.getLocalPref();              // Restore

        //
        // Compare two routes with different AS_PATH length
        //
        ArrayList<BgpRouteEntry.PathSegment> pathSegments2 = new ArrayList<>();
        pathSegments2.add(pathSegment1);
        // Different AS Path
        BgpRouteEntry.AsPath asPath2 = new BgpRouteEntry.AsPath(pathSegments2);
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath2,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(false));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(true));

        //
        // Compare two routes with different ORIGIN
        //
        origin = BgpConstants.Update.Origin.EGP;                // Different
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(false));
        origin = bgpRouteEntry1.getOrigin();                    // Restore

        //
        // Compare two routes with different MULTI_EXIT_DISC
        //
        multiExitDisc = 10;                                     // Different
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(false));
        multiExitDisc = bgpRouteEntry1.getMultiExitDisc();      // Restore

        //
        // Compare two routes with different BGP ID
        //
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession2, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(false));

        //
        // Compare two routes with different BGP address
        //
        bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession3, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);
        //
        assertThat(bgpRouteEntry1.isBetterThan(bgpRouteEntry2), is(true));
        assertThat(bgpRouteEntry2.isBetterThan(bgpRouteEntry1), is(false));
    }

    /**
     * Tests equality of {@link BgpRouteEntry}.
     */
    @Test
    public void testEquality() {
        BgpRouteEntry bgpRouteEntry1 = generateBgpRouteEntry();
        BgpRouteEntry bgpRouteEntry2 = generateBgpRouteEntry();

        assertThat(bgpRouteEntry1, is(bgpRouteEntry2));
    }

    /**
     * Tests non-equality of {@link BgpRouteEntry}.
     */
    @Test
    public void testNonEquality() {
        BgpRouteEntry bgpRouteEntry1 = generateBgpRouteEntry();

        // Setup BGP Route 2
        IpPrefix prefix = IpPrefix.valueOf("1.2.3.0/24");
        IpAddress nextHop = IpAddress.valueOf("5.6.7.8");
        byte origin = BgpConstants.Update.Origin.IGP;
        // Setup the AS Path
        ArrayList<BgpRouteEntry.PathSegment> pathSegments = new ArrayList<>();
        byte pathSegmentType1 = (byte) BgpConstants.Update.AsPath.AS_SEQUENCE;
        ArrayList<Long> segmentAsNumbers1 = new ArrayList<>();
        segmentAsNumbers1.add((long) 1);
        segmentAsNumbers1.add((long) 2);
        segmentAsNumbers1.add((long) 3);
        BgpRouteEntry.PathSegment pathSegment1 =
            new BgpRouteEntry.PathSegment(pathSegmentType1, segmentAsNumbers1);
        pathSegments.add(pathSegment1);
        //
        byte pathSegmentType2 = (byte) BgpConstants.Update.AsPath.AS_SET;
        ArrayList<Long> segmentAsNumbers2 = new ArrayList<>();
        segmentAsNumbers2.add((long) 4);
        segmentAsNumbers2.add((long) 5);
        segmentAsNumbers2.add((long) 6);
        BgpRouteEntry.PathSegment pathSegment2 =
            new BgpRouteEntry.PathSegment(pathSegmentType2, segmentAsNumbers2);
        pathSegments.add(pathSegment2);
        //
        BgpRouteEntry.AsPath asPath = new BgpRouteEntry.AsPath(pathSegments);
        //
        long localPref = 500;                                   // Different
        long multiExitDisc = 20;
        BgpRouteEntry bgpRouteEntry2 =
            new BgpRouteEntry(bgpSession, prefix, nextHop, origin, asPath,
                              localPref);
        bgpRouteEntry2.setMultiExitDisc(multiExitDisc);

        assertThat(bgpRouteEntry1, is(not(bgpRouteEntry2)));
    }

    /**
     * Tests object string representation.
     */
    @Test
    public void testToString() {
        BgpRouteEntry bgpRouteEntry = generateBgpRouteEntry();

        String expectedString =
            "BgpRouteEntry{prefix=1.2.3.0/24, nextHop=5.6.7.8, " +
            "bgpId=10.0.0.1, origin=0, asPath=AsPath{pathSegments=" +
            "[PathSegment{type=2, segmentAsNumbers=[1, 2, 3]}, " +
            "PathSegment{type=1, segmentAsNumbers=[4, 5, 6]}]}, " +
            "localPref=100, multiExitDisc=20}";
        assertThat(bgpRouteEntry.toString(), is(expectedString));
    }
}
