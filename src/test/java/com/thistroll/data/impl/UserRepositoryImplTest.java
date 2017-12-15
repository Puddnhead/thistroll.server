package com.thistroll.data.impl;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.thistroll.data.util.DynamoDBConnectionProvider;
import com.thistroll.domain.User;
import com.thistroll.domain.enums.UserRole;
import com.thistroll.exceptions.DuplicateEmailException;
import com.thistroll.exceptions.DuplicateUsernameException;
import com.thistroll.exceptions.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Not strictly a unit test, as a lot of these tests test logic in the UserValidationUtil
 *
 * Created by MVW on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryImplTest extends AbstractRepositoryTest {

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Mock
    private Index mockUsernameIndex;

    @Mock
    private Index mockEmailIndex;

    @Mock
    private ItemCollection<QueryOutcome> mockItemCollection;

    @Mock
    private ItemCollection<QueryOutcome> mockItemCollectionWithoutItems;

    @Mock
    private IteratorSupport<Item, QueryOutcome> mockIteratorSupport;

    @Mock
    private IteratorSupport<Item, QueryOutcome> mockIteratorSupportWithoutItems;

    private static final String ID = UUID.randomUUID().toString();
    private static final String USERNAME = "BobbyBriscuit";
    private static final String FIRST_NAME = "Bobby";
    private static final String LAST_NAME = "Briscuit";
    private static final String EMAIL = "bobby@briscuit.com";
    private static final Set<UserRole> ROLES = Collections.singleton(UserRole.USER);
    private static final boolean NOTIFICATIONS_ENABLED = true;
    private static final String PASSWORD = "password123";

    @Override
    void setConnectionProvider(DynamoDBConnectionProvider dynamoDBConnectionProvider) {
        userRepository.setConnectionProvider(dynamoDBConnectionProvider);
    }

    @Before
    public void setup() throws Exception {
        // By default these indexes will not return items. Tests need to stub the index to return the
        // mockItemCollection and then stub the return values
        when(mockTable.getIndex(User.USERNAME_INDEX)).thenReturn(mockUsernameIndex);
        when(mockTable.getIndex(User.EMAIL_INDEX)).thenReturn(mockEmailIndex);
        when(mockUsernameIndex.query(any(QuerySpec.class))).thenReturn(mockItemCollection);
        when(mockEmailIndex.query(any(QuerySpec.class))).thenReturn(mockItemCollection);
        when(mockItemCollection.iterator()).thenReturn(mockIteratorSupport);
        when(mockItemCollectionWithoutItems.iterator()).thenReturn(mockIteratorSupportWithoutItems);
        when(mockIteratorSupport.hasNext()).thenReturn(true);
        when(mockIteratorSupportWithoutItems.hasNext()).thenReturn(false);
    }

    @Test
    public void testCreateUserGeneratesIdAndDatesAndHashesPassword() throws Exception {
        final String IGNORED_ID = "abc";
        final DateTime IGNORED_DATE = new DateTime(0);

        User suppliedUser = createDefaultUserBuilder()
                .id(IGNORED_ID)
                .createdOn(IGNORED_DATE)
                .lastUpdatedOn(IGNORED_DATE)
                .build();

        when(mockTable.putItem(itemCaptor.capture())).thenReturn(mock(PutItemOutcome.class));

        User user = userRepository.createUser(suppliedUser, PASSWORD);
        assertThat(user.getId(), is(not(IGNORED_ID)));
        assertThat(user.getCreatedOn(), is(not(IGNORED_DATE.getMillis())));
        assertThat(user.getLastUpdatedOn(), is(not(IGNORED_DATE.getMillis())));
        assertThat(user.getUsername(), is(USERNAME));
        assertThat(user.getFirstName(), is(FIRST_NAME));
        assertThat(user.getLastName(), is(LAST_NAME));
        assertThat(user.getEmail(), is(EMAIL));
        assertThat(user.getRoles(), is(ROLES));
        assertThat(user.isNotificationsEnabled(), is(NOTIFICATIONS_ENABLED));

        Item item = itemCaptor.getValue();
        assertThat(item.getString(User.PASSWORD_PROPERTY), is(not(PASSWORD)));
        assertThat(item.getString(User.ID_PROPERTY), is(not(IGNORED_ID)));
        assertThat(item.getLong(User.CREATED_ON_PROPERTY), is(not(IGNORED_DATE.getMillis())));
        assertThat(item.getLong(User.LAST_UPDATED_ON_PROPERTY), is(not(IGNORED_DATE.getMillis())));
        assertThat(item.getString(User.USERNAME_PROPERTY), is(USERNAME));
        assertThat(item.getString(User.FIRST_NAME_PROPERTY), is(FIRST_NAME));
        assertThat(item.getString(User.LAST_NAME_PROPERTY), is(LAST_NAME));
        assertThat(item.getString(User.EMAIL_PROPERTY), is(EMAIL));
        assertThat(item.getString(User.ROLES_PROPERTY), is(ROLES.iterator().next().name()));
        assertThat(item.getBoolean(User.NOTIFICATIONS_PROPERTY), is(NOTIFICATIONS_ENABLED));
    }

    @Test(expected = ValidationException.class)
    public void testCreateUserUsernameMustBeAlphanumeric() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .username("*user")
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testCreateUserPasswordIsAtLeast6Characters() throws Exception {
        User invalidUser = createDefaultUserBuilder().build();
        userRepository.createUser(invalidUser, "123");
    }

    @Test(expected = ValidationException.class)
    public void testMaximumUsernameLength() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .username(StringUtils.repeat('a', 257))
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testMinimumUsernameLength() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .username("b")
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testMaximumFirstNameLength() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .firstName(StringUtils.repeat('a', 257))
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testMaximumLastNameLength() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .firstName(StringUtils.repeat('a', 257))
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testEmailFormat() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .email("JoeatJoedotcom")
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test(expected = ValidationException.class)
    public void testEmailRequired() throws Exception {
        User invalidUser = createDefaultUserBuilder()
                .email(null)
                .build();
        userRepository.createUser(invalidUser, PASSWORD);
    }

    @Test
    public void testGetByIdWithNullValues() throws Exception {
        Item item = createItemWithCommonValues();
        when(mockTable.getItem(any(GetItemSpec.class))).thenReturn(item);

        User user = userRepository.getUserById(ID);
        assertCommonFields(user);
    }

    @Test
    public void testGetByUsernameWithNullValues() throws Exception {
        Item item = createItemWithCommonValues();
        when(mockIteratorSupport.hasNext()).thenReturn(true);
        when(mockIteratorSupport.next()).thenReturn(item);

        User user = userRepository.getUserByUsername(USERNAME);
        assertCommonFields(user);
    }

    @Test
    public void testGetByEmailWithNullValues() throws Exception {
        Item item = createItemWithCommonValues();
        when(mockIteratorSupport.hasNext()).thenReturn(true);
        when(mockIteratorSupport.next()).thenReturn(item);

        User user = userRepository.getUserByEmail(EMAIL);
        assertCommonFields(user);
    }

    @Test(expected = DuplicateUsernameException.class)
    public void testCreateUserFailsForDuplicateUsername() throws Exception {
        when(mockEmailIndex.query(any(QuerySpec.class))).thenReturn(mockItemCollectionWithoutItems);
        generateDuplicateException();
    }

    @Test(expected = DuplicateEmailException.class)
    public void testCreateUserFailsForDuplicateEmail() throws Exception {
        when(mockUsernameIndex.query(any(QuerySpec.class))).thenReturn(mockItemCollectionWithoutItems);
        generateDuplicateException();
    }

    private void generateDuplicateException() {
        Item mockItem = mock(Item.class);
        when(mockItem.getString(User.ID_PROPERTY)).thenReturn(ID);
        when(mockIteratorSupport.next()).thenReturn(mockItem);

        User user = createDefaultUserBuilder().build();
        userRepository.createUser(user, PASSWORD);
    }

    @Test
    public void testGetUserWithCredentials() throws Exception {
        Item item = createItemWithCommonValues()
                .withString(User.PASSWORD_PROPERTY, UserRepositoryImpl.hashPassword(PASSWORD));
        when(mockIteratorSupport.hasNext()).thenReturn(true);
        when(mockIteratorSupport.next()).thenReturn(item);
        User user = userRepository.getUserWithCredentials(USERNAME, PASSWORD);
        assertCommonFields(user);
    }

    @Test
    public void testGetUserWithCredentialsMissingUserReturnsNull() throws Exception {
        when(mockIteratorSupport.hasNext()).thenReturn(false);
        User user = userRepository.getUserWithCredentials(USERNAME, PASSWORD);
        assertThat(user, is(nullValue()));
    }

    @Test
    public void testGetUserWithCredentialsInvalidPasswordReturnsNull() throws Exception {
        Item item = createItemWithCommonValues()
                .withString(User.PASSWORD_PROPERTY, "randomPassword");
        when(mockIteratorSupport.hasNext()).thenReturn(true);
        when(mockIteratorSupport.next()).thenReturn(item);
        User user = userRepository.getUserWithCredentials(USERNAME, PASSWORD);
        assertThat(user, is(nullValue()));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<Map<String, AttributeValue>> attributeValueMaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            attributeValueMaps.add(createAttributeValueMap());
        }
        ScanResult scanResult = mock(ScanResult.class);
        when(scanResult.getItems()).thenReturn(attributeValueMaps);
        stubFetchingUsersForTenUsers(scanResult);

        List<User> users = userRepository.getAllUsers(Optional.empty(), Optional.empty());
        assertThat(users.size(), is(10));
    }

    @Test
    public void testGetAllUsersPagination() throws Exception {
        List<Map<String, AttributeValue>> attributeValueMaps = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            attributeValueMaps.add(createAttributeValueMap());
        }
        ScanResult scanResult = mock(ScanResult.class);
        when(scanResult.getItems()).thenReturn(attributeValueMaps);

        stubFetchingUsersForTenUsers(scanResult);

        List<User> users = userRepository.getAllUsers(Optional.of(2), Optional.of(4));
        assertThat(users.size(), is(2));
    }

    private void stubFetchingUsersForTenUsers(ScanResult scanResult) throws Exception {
        doAnswer(invocationOnMock -> {
            ScanRequest scanRequest = (ScanRequest)invocationOnMock.getArguments()[0];
            if (scanRequest.getExclusiveStartKey() == null) {
                when(scanResult.getLastEvaluatedKey())
                        .thenReturn(Collections.singletonMap(User.ID_PROPERTY, new AttributeValue().withS(ID)));
            } else {
                when(scanResult.getLastEvaluatedKey()).thenReturn(null);
            }
            return scanResult;
        }).when(amazonDynamoDB).scan(any(ScanRequest.class));
    }

    private void assertCommonFields(User user) {
        assertThat(user.getId(), is(ID));
        assertThat(user.getUsername(), is(USERNAME));
        assertThat(user.getEmail(), is(EMAIL));
        assertThat(user.isNotificationsEnabled(), is(NOTIFICATIONS_ENABLED));
    }

    private User.Builder createDefaultUserBuilder() {
        return new User.Builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .roles(ROLES)
                .notificationsEnabled(NOTIFICATIONS_ENABLED)
                .email(EMAIL);
    }

    private Item createItemWithCommonValues() {
        return new Item()
                .withPrimaryKey(User.ID_PROPERTY, ID)
                .withString(User.EMAIL_PROPERTY, EMAIL)
                .withString(User.USERNAME_PROPERTY, USERNAME)
                .withBoolean(User.NOTIFICATIONS_PROPERTY, NOTIFICATIONS_ENABLED);
    }

    private Map<String, AttributeValue> createAttributeValueMap() {
        Map<String, AttributeValue> attributeValueMap = new HashMap<>();
        attributeValueMap.put(User.ID_PROPERTY, new AttributeValue().withS(ID));
        attributeValueMap.put(User.EMAIL_PROPERTY, new AttributeValue().withS(EMAIL));
        attributeValueMap.put(User.USERNAME_PROPERTY, new AttributeValue().withS(USERNAME));
        attributeValueMap.put(User.NOTIFICATIONS_PROPERTY, new AttributeValue().withBOOL(NOTIFICATIONS_ENABLED));
        return attributeValueMap;
    }
}