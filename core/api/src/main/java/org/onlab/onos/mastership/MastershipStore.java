package org.onlab.onos.mastership;

import java.util.List;
import java.util.Set;

import org.onlab.onos.cluster.NodeId;
import org.onlab.onos.net.DeviceId;
import org.onlab.onos.net.MastershipRole;
import org.onlab.onos.store.Store;

/**
 * Manages inventory of mastership roles for devices, across controller
 * instances; not intended for direct use.
 */
public interface MastershipStore extends Store<MastershipEvent, MastershipStoreDelegate> {

    // three things to map: NodeId, DeviceId, MastershipRole

    /**
     * Requests role of the local node for the specified device.
     *
     * @param deviceId device identifier
     * @return established or newly negotiated mastership role
     */
    MastershipRole requestRole(DeviceId deviceId);

    /**
     * Returns the role of a device for a specific controller instance.
     *
     * @param nodeId   the instance identifier
     * @param deviceId the device identifiers
     * @return the role
     */
    MastershipRole getRole(NodeId nodeId, DeviceId deviceId);

    /**
     * Returns the master for a device.
     *
     * @param deviceId the device identifier
     * @return the instance identifier of the master
     */
    NodeId getMaster(DeviceId deviceId);

    /**
     * Returns the controllers connected to a device, in mastership-
     * preference order.
     *
     * @param deviceId the device identifier
     * @return an ordered list of controller IDs
     */
    List<NodeId> getNodes(DeviceId deviceId);

    /**
     * Returns the devices that a controller instance is master of.
     *
     * @param nodeId the instance identifier
     * @return a set of device identifiers
     */
    Set<DeviceId> getDevices(NodeId nodeId);


    /**
     * Sets a device's role for a specified controller instance.
     *
     * @param nodeId   controller instance identifier
     * @param deviceId device identifier
     * @return a mastership event
     */
    MastershipEvent setMaster(NodeId nodeId, DeviceId deviceId);

    /**
     * Returns the current master and number of past mastership hand-offs
     * (terms) for a device.
     *
     * @param deviceId the device identifier
     * @return the current master's ID and the term value for device, or null
     */
    MastershipTerm getTermFor(DeviceId deviceId);

    /**
     * Sets a controller instance's mastership role to STANDBY for a device.
     * If the role is MASTER, another controller instance will be selected
     * as a candidate master.
     *
     * @param nodeId   the controller instance identifier
     * @param deviceId device to revoke mastership role for
     * @return a mastership event
     */
    MastershipEvent setStandby(NodeId nodeId, DeviceId deviceId);

    /**
     * Allows a controller instance to give up its current role for a device.
     * If the role is MASTER, another controller instance will be selected
     * as a candidate master.
     *
     * @param nodeId   the controller instance identifier
     * @param deviceId device to revoke mastership role for
     * @return a mastership event
     */
    MastershipEvent relinquishRole(NodeId nodeId, DeviceId deviceId);

}
