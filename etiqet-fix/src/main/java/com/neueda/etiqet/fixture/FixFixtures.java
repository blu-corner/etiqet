package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.util.ParserUtils;
import cucumber.api.java.en.Then;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        Cdr message = getMessage(messageName);
        CdrItem group = getGroup(message, messageName, existingGroup);
        List<Cdr> cdrs = group.getCdrs();
        if (cdrs == null) {
            cdrs = new ArrayList<>();
        }
        Cdr newGroup = new Cdr(groupType);
        addGroupToCdr(groupType, newGroup);
        cdrs.add(newGroup);
        group.setCdrs(cdrs);
        message.setItem(existingGroup, group);
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

        CdrItem group = getGroup(message, messageName, groupType);

        Cdr fields = ParserUtils.stringToCdr(groupType, handlers.preTreatParams(params));
        List<Cdr> cdrs = group.getCdrs();
        if (cdrs == null) {
            cdrs = new ArrayList<>();
        }
        cdrs.add(fields);
        group.setCdrs(cdrs);
        message.getItems().put(groupType, group);
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
     * @param messageName Name of the message
     * @param groupName   Name of the group to get
     * @return {@link CdrItem} that represents the group
     * @throws AssertionError when the group doesn't exist within the message, or the {@code CdrItem} found does not
     *                        have the type {@code CDR_ARRAY}
     */
    CdrItem getGroup(Cdr message, String messageName, String groupName) {
        if(groupName.contains("/")) {
            String[] groupSplit = groupName.split("/");
            String firstGroup = groupSplit[0];
            String[] remainingGroups = Arrays.copyOfRange(groupSplit, 1, groupSplit.length);
            return getNestedGroup(message, messageName, firstGroup, remainingGroups);
        }

        CdrItem group = message.getItem(groupName);
        assertNotNull("Could not find group " + groupName + " in message " + messageName, group);
        assertEquals("Requested " + groupName + " doesn't appear to be a group, was " + group.getType(),
                     CdrItem.CdrItemType.CDR_ARRAY, group.getType());
        return group;
    }

    /**
     *
     * @param message
     * @param messageName
     * @param groupName
     * @param remainingGroups
     * @return
     */
    CdrItem getNestedGroup(Cdr message, String messageName, String groupName, String[] remainingGroups) {
        CdrItem group = message.getItem(groupName);
        assertNotNull("Could not find group " + groupName + " in message " + messageName, group);
        assertEquals("Requested " + groupName + " doesn't appear to be a group, was " + group.getType(),
                     CdrItem.CdrItemType.CDR_ARRAY, group.getType());

        if(remainingGroups.length > 0) {
            String nextGroup = remainingGroups[0];
            String[] groups = Arrays.copyOfRange(remainingGroups, 1, remainingGroups.length);
            Cdr nextMessage = group.getCdrs()
                                   .stream()
                                   .filter(cdr -> cdr.containsKey(nextGroup))
                                   .findFirst()
                                   .orElse(null);
            assertNotNull("Could not find group " + nextGroup + " in group " + groupName, nextMessage);
            return getNestedGroup(nextMessage, groupName, nextGroup, groups);
        }

        return group;
    }

    private void getGroupWithFilter(Cdr message, String groupName) {
        if(!groupName.contains("[") && !groupName.contains("]")) {
            return;
        }

        Pattern pattern = Pattern.compile("(.*)(\\[.*])*");

    }

}
