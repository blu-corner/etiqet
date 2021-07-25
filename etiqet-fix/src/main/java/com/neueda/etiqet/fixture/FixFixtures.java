package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.core.util.StringUtils;
import cucumber.api.java.en.Then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Class that contains Etiqet Fixtures specific to the FIX protocol.
 */
public class FixFixtures {

    private final EtiqetHandlers handlers;

    public FixFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    /**
     * <p>Creates an empty group of the given type in the message specified.</p>
     *
     * <p><b>Example usage:</b> <code>Then create a repeating group "NoSides" in message "TradeCaptureReport"</code></p>
     *
     * @param groupType   Name of the repeating group
     * @param messageName Name of the message to add the group to
     */
    @Then("create a repeating group \"([^\"]*)\" in message \"([^\"]*)\"")
    public void createRepeatingGroup(String groupType, String messageName) {
        Cdr message = getMessage(messageName);
        addGroupToCdr(groupType, message);
    }

    /**
     * <p>Creates an empty group of the given type in the message specified.</p>
     *
     * <p><b>Example usage:</b> <code>Then create a repeating group "NoSides" with "Side=1,Currency=GBP" in message "TradeCaptureReport"</code></p>
     *
     * @param groupType   Name of the repeating group
     * @param params      Field values to go into the new group
     * @param messageName Name of the message to add the group to
     */
    @Then("create a repeating group \"([^\"]*)\" with \"([^\"]*)\" in message \"([^\"]*)\"")
    public void createRepeatingGroupWithFields(String groupType, String params, String messageName) {
        Cdr message = getMessage(messageName);
        CdrItem groupItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr child = ParserUtils.stringToCdr(groupType, handlers.preTreatParams(params));
        groupItem.addCdrToList(child);
        message.setItem(groupType, groupItem);
    }

    /**
     * <p>Creates an empty group of the given type into a pre-existing group within the message specified.</p>
     *
     * <p><b>Example usage:</b> <code>Then create a repeating group "Parties" in group "NoSides" in message "TradeCaptureReport"</code></p>
     *
     * <p>Group types may use XPath style mappings for nested groups, e.g. <code>NoSides/Parties[PartyID=MyBank123]</code></p>
     *
     * @param groupType     Name of the new repeating group to be added
     * @param existingGroup Name of the existing repeating group the new group should be added to
     * @param messageName   Name of the message to add the group to
     */
    @Then("create a repeating group \"([^\"]*)\" in group \"([^\"]*)\" in message \"([^\"]*)\"")
    public void createRepeatingGroupInGroup(String groupType, String existingGroup, String messageName) {
        createRepeatingGroupWithFieldsInGroup(groupType, null, existingGroup, messageName);
    }

    /**
     * <p>Creates a group with the fields provided of the given type into a pre-existing group within the message
     * specified</p>
     *
     * <p><b>Example usage:</b> <code>Then create a repeating group "Parties" with "PartyID=123,PartyRole=13" in group "NoSides" in message "TradeCaptureReport"</code></p>
     *
     * <p>Group types may use XPath style mappings for nested groups, e.g. <code>NoSides/Parties[PartyID=MyBank123]</code></p>
     *
     * @param groupType     Name of the new repeating group to be added
     * @param params        Field values to go into the new group
     * @param existingGroup Name of the existing repeating group the new group should be added to
     * @param messageName   Name of the message to add the group to
     */
    @Then("create a repeating group \"([^\"]*)\" with \"([^\"]*)\" in group \"([^\"]*)\" in message \"([^\"]*)\"")
    public void createRepeatingGroupWithFieldsInGroup(String groupType, String params, String existingGroup, String messageName) {
        Cdr message = getMessage(messageName);
        Cdr group = getGroup(message, messageName, existingGroup);
        Cdr fields = ParserUtils.stringToCdr(groupType, handlers.preTreatParams(params));
        if (group.containsKey(groupType)) {
            group.getItem(groupType).addCdrToList(fields);
        } else {
            CdrItem cdrItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
            cdrItem.addCdrToList(fields);
            group.setItem(groupType, cdrItem);
        }
    }

    /**
     * <p>Creates a group with the fields provided of the given type into a pre-existing group within the message
     * specified</p>
     *
     * <p><b>Example usage:</b> <code>Then create a repeating group "Parties" with "PartyID=123,PartyRole=13" in group "NoSides" in message "TradeCaptureReport"</code></p>
     *
     * <p>Group types may use XPath style mappings for nested groups, e.g. <code>NoSides/Parties[PartyID=MyBank123]</code></p>
     *
     * @param groupType     Name of the new repeating group to be added
     * @param params        Field values to go into the new group
     * @param existingGroup Name of the existing repeating group the new group should be added to
     * @param messageName   Name of the message to add the group to
     */
    @Then("create a repeating group \"([^\"]*)\" with \"([^\"]*)\" in group \"([^\"]*)\" at index (\\d+) in message \"([^\"]*)\"")
    public void createRepeatingGroupWithFieldsInGroupAtIndex(String groupType, String params, String existingGroup, int index, String messageName) {
        Cdr message = getMessage(messageName);
        Cdr group = getGroupAtIndex(message, messageName, existingGroup, index);
        Cdr fields = ParserUtils.stringToCdr(groupType, handlers.preTreatParams(params));
        if (group.containsKey(groupType)) {
            group.getItem(groupType).addCdrToList(fields);
        } else {
            CdrItem cdrItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
            cdrItem.addCdrToList(fields);
            group.setItem(groupType, cdrItem);
        }
    }

    /**
     * <p>Adds fields to a group within the message specified.</p>
     *
     * <p><b>Example usage:</b> <code>Then add fields "PartyID=MyBank123,PartyRole=13" to "Parties" group in message "TradeCaptureReport"</code></p>
     *
     * <p>Group types may use XPath style mappings for nested groups, e.g. <code>NoSides/Parties[PartyID=MyBank123]</code></p>
     *
     * @param params      Comma-separated list of <code>field=value</code> fields
     * @param groupType   Group the fields should be added
     * @param messageName Name of the message to add the fields to
     */
    @Then("add fields \"([^\"]*)\" to \"([^\"]*)\" group in message \"([^\"]*)\"")
    public void addFieldsToRepeatingGroup(String params, String groupType, String messageName) {
        Cdr message = getMessage(messageName);
        Cdr fields = ParserUtils.stringToCdr(groupType, handlers.preTreatParams(params));
        if (message.containsKey(groupType)) {
            message.getItem(groupType).addCdrToList(fields);
            return;
        }

        Cdr group = getGroup(message, messageName, groupType);
        if (group.containsKey(groupType)) {
            group.getItem(groupType).addCdrToList(fields);
        } else {
            CdrItem cdrItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
            cdrItem.addCdrToList(fields);
            group.setItem(groupType, cdrItem);
        }
    }

    /**
     * Adds a new empty group to the Cdr passed in
     *
     * @param groupType Type of group to be added to the message
     * @param message   CDR to add the group to
     */
    void addGroupToCdr(String groupType, Cdr message) {
        assertNotNull("No message found to add group " + groupType, message);
        CdrItem groupItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        groupItem.setCdrs(new ArrayList<>());
        message.setItem(groupType, groupItem);
    }

    /**
     * Gets a specified message from the {@code EtiqetHandlers}
     *
     * @param messageName name of the message to get from EtiqetHandlers
     * @return {@link Cdr} representation of the message
     * @throws AssertionError when the message doesn't exist
     */
    private Cdr getMessage(String messageName) {
        Cdr message = handlers.getSentMessage(messageName);
        assertNotNull("Could not find message " + messageName, message);

        return message;
    }

    /**
     * Gets a group from the message specified
     *
     * @param message     Message to get the group from
     * @param messageName Name of the message, for logging purposes only
     * @param groupName   Name of the group to get
     * @return {@link Cdr} that represents the group
     * @throws AssertionError when the group doesn't exist within the message, or the {@code CdrItem} found does not
     *                        have the type {@code CDR_ARRAY}
     */
    Cdr getGroup(Cdr message, String messageName, String groupName) {
        if (groupName.contains("/")) {
            return getGroupWithXPath(message, messageName, groupName);
        }

        CdrItem item = message.getItem(groupName);
        if (item.getCdrs() == null || !item.getType().equals(CdrItem.CdrItemType.CDR_ARRAY)) {
            fail("Requested " + groupName + " doesn't appear to be a group");
        }
        Optional<Cdr> group = item.getCdrs()
            .stream()
            .filter(cdr -> cdr.getType().equals(groupName))
            .findFirst();
        if (group.isPresent()) {
            return group.get();
        } else {
            Cdr newGroup = new Cdr(groupName);
            item.addCdrToList(newGroup);
            return newGroup;
        }
    }

    /**
     * Gets a group from the message specified
     *
     * @param message     Message to get the group from
     * @param messageName Name of the message, for logging purposes only
     * @param groupName   Name of the group to get
     * @param index       Index of the group to get
     * @return {@link Cdr} that represents the group
     * @throws AssertionError when the group doesn't exist within the message, or the {@code CdrItem} found does not
     *                        have the type {@code CDR_ARRAY}
     */
    Cdr getGroupAtIndex(Cdr message, String messageName, String groupName, int index) {
        if (groupName.contains("/")) {
            return getGroupWithXPath(message, messageName, groupName);
        }

        CdrItem item = message.getItem(groupName);
        if (item.getCdrs() == null || !item.getType().equals(CdrItem.CdrItemType.CDR_ARRAY)) {
            fail("Requested " + groupName + " doesn't appear to be a group");
        }
        List<Cdr> cdrs = item.getCdrs()
            .stream()
            .filter(cdr -> cdr.getType().equals(groupName))
            .collect(Collectors.toList());
        if (index >= cdrs.size()) {
            throw new IndexOutOfBoundsException("Group at index " + index + " does not exist in groups: " + cdrs);
        } else {
            return cdrs.get(index);
        }
    }

    /**
     * Gets a group from the message specified
     *
     * @param message     Message to get the group from
     * @param messageName Name of the message, for logging purposes only
     * @param groupName   Name of the group to get
     * @return {@link Cdr} that represents the group
     * @throws AssertionError when the group doesn't exist within the message, or the {@code CdrItem} found does not
     *                        have the type {@code CDR_ARRAY}
     */
    Cdr getGroupWithXPath(Cdr message, String messageName, String groupName) {
        String[] groupSplit = groupName.split("/");
        String firstGroup = groupSplit[0];
        String[] remainingGroups = Arrays.copyOfRange(groupSplit, 1, groupSplit.length);
        return getNestedGroup(message, messageName, firstGroup, remainingGroups);
    }

    /**
     * Gets a nested group from the message provided
     *
     * @param message         Cdr message
     * @param messageName     name of the message, for logging purposes only
     * @param groupName       Initial group name
     * @param remainingGroups Remaining groups, if split by <code>/</code> character
     * @return CdrItem representing the group
     */
    private Cdr getNestedGroup(Cdr message, String messageName, String groupName, String[] remainingGroups) {
        Cdr group = getGroupWithFilter(message, groupName);
        assertNotNull("Could not find group " + groupName + " in message " + messageName, group);

        if (remainingGroups.length > 0) {
            String nextGroup = remainingGroups[0];
            String[] groups = Arrays.copyOfRange(remainingGroups, 1, remainingGroups.length);
            CdrItem nextGroupItem = group.getItem(getUnfilteredGroupName(nextGroup));
            assertNotNull("Could not find group " + nextGroup + " in group " + groupName, nextGroupItem);
            assertEquals(nextGroup + " appears not to be an array type",
                CdrItem.CdrItemType.CDR_ARRAY,
                nextGroupItem.getType());

            Cdr nextMessage = getGroupWithFilter(message, groupName);
            return getNestedGroup(nextMessage, groupName, nextGroup, groups);
        }

        return group;
    }

    /**
     * Returns a {@link CdrItem} matching the group name
     *
     * @param message   {@link Cdr} message that the group should belong to
     * @param groupName name of the group to get from the message. If using a filter should be defined as
     *                  <code>group[field=name]</code>
     * @return Matching Cdr for the group
     */
    Cdr getGroupWithFilter(Cdr message, String groupName) {
        if (!groupName.contains("[") && !groupName.contains("]")) {
            return message.getItem(groupName).getCdrs().get(0);
        }

        int filterStart = groupName.indexOf("[");
        int filterEnd = groupName.indexOf("]");
        assertNotEquals("Could not find start of filter '['", -1, filterStart);
        assertNotEquals("Could not find end of filter ']'", -1, filterEnd);
        String name = groupName.substring(0, filterStart);
        String filter = groupName.substring(filterStart + 1, filterEnd);

        String[] split = filter.split("=");
        String field = split[0];
        assertFalse("No field specified in group filter " + groupName, StringUtils.isNullOrEmpty(field));

        assertEquals("No value specified in group filter " + groupName, 2, split.length);
        String value = split[1];
        CdrItem childItem = message.getItem(name);
        assertEquals("Field " + name + " was not a group", CdrItem.CdrItemType.CDR_ARRAY, childItem.getType());
        Cdr child = childItem.getCdrs()
            .stream()
            .filter(cdr -> cdr.containsKey(field) && cdr.getAsString(field).equals(value))
            .findFirst()
            .orElse(null);
        assertNotNull("Could not find matching group " + groupName, child);
        return child;
    }

    private String getUnfilteredGroupName(String groupName) {
        if (!groupName.contains("[") && !groupName.contains("]")) {
            return groupName;
        }
        int filterStart = groupName.indexOf("[");
        assertNotEquals("Could not find start of filter '['", -1, filterStart);
        return groupName.substring(0, filterStart);
    }

}
