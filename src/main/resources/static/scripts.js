fetch('http://localhost:9000/users')
    .then(res => res.json())
    .then(users => {
        let tableHtml = ''

        for (let index in users) {
            const user = users[index]
            tableHtml += userRow(user)
        }

        document.getElementById('usersBody').innerHTML = tableHtml
    })

function searchByFirstName(){
    let select = document.getElementById('sortOptions');
    let selectedSortOption = select.options[select.selectedIndex].value;

    const getSearch = document.getElementById('search').value;

    fetch(`http://localhost:9000/users/?${selectedSortOption}=${getSearch}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(res => res.json())
        .then(res => {
            let tableHtml = ''
            for (let index in res) {
                const user = res[index]
                tableHtml += userRow(user)
            }
            document.getElementById('usersBody').innerHTML = tableHtml
        })
}

function userRow(user) {

    const userId = user.id

    const tr = `<tr id="user-${userId}">
  <td>${userId}</td><td class="firstName">${user.firstName}</td>
  <td class="lastName">${user.lastName}</td>
  <td style="width: 25px; height: 25px padding: 5px;"><button onclick="infoForm(${userId})" id="update" type="button">\u2139</button></td>
  <td style="width: 25px; height: 25px padding: 5px;"><button onclick="openForm(${userId})" id="update" type="button">\u2700</button></td>
  <td style="width: 25px; height: 25px; padding: 5px;"><button onclick="
  deleteUser(${userId})" id="delete" type="button"> \u2718 </button></td>
</tr>`

    return tr
}

function deleteUser(userId) {

    fetch(`http://localhost:9000/users/${userId}`, {
        method: 'DELETE'
    })
        .then(() => {
            let row = document.getElementById(`user-${userId}`)
            row.remove();
        })
}

function updateUser(userId) {
    const newFirstName = document.getElementById('newFirstName').value
    const newLastName = document.getElementById('newLastName').value
    const newEmail = document.getElementById('newEmail').value
    const newLogin = document.getElementById('newLogin').value
    const newCountry = document.getElementById('newCountry').value
    const newCity = document.getElementById('newCity').value
    const newStreet = document.getElementById('newStreet').value
    const newHouseNumber = document.getElementById('newHouseNumber').value
    const newZipCode = document.getElementById('newZipCode').value

    fetch(`http://localhost:9000/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            firstName: newFirstName,
            lastName: newLastName,
            login: newLogin,
            email: newEmail,
            address: {
                country: newCountry,
                city: newCity,
                street: newStreet,
                houseNumber: newHouseNumber,
                zipCode: newZipCode
            }
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { return Promise.reject(text) })
            } else {
                return response.json();
            }
        })
        .then(user => {
                // wyswietlanie w tabelce
                const row = document.getElementById(`user-${userId}`)
                row.querySelector('.firstName').innerHTML = user.firstName
                row.querySelector('.lastName').innerHTML = user.lastName
        })
        .catch(error => {
            if (error === 'EMAIL_EXISTS') {
                console.log('Email exists, try different one :(')
            } else if (error === 'LOGIN_EXISTS') {
                console.log('Login exists, try different one :(')
            } else {
                console.error('Error occurred', error)
            }
        })
}

function openForm(userId) {
    fetch(`http://localhost:9000/users/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(res => res.json())
        .then(res => {
            // response = { id, firstName, lastName, login, email, address }
            const updateUserForm = document.getElementById("updateForm");
            const row = document.getElementById(`user-${userId}`)
            //wyswietlanie value w formie
            updateUserForm.querySelector('#newFirstName').value = res.firstName
            updateUserForm.querySelector('#newLastName').value = res.lastName
            updateUserForm.querySelector('#newLogin').value = res.login
            updateUserForm.querySelector('#newEmail').value = res.email
            updateUserForm.querySelector('#newCountry').value = res.address.country
            updateUserForm.querySelector('#newCity').value = res.address.city
            updateUserForm.querySelector('#newStreet').value = res.address.street
            updateUserForm.querySelector('#newHouseNumber').value = res.address.houseNumber
            updateUserForm.querySelector('#newZipCode').value = res.address.zipCode
            updateUserForm.style.display = "block"
            const submitButton = updateUserForm.querySelector('button[type="submit"]')

            let newSubmitButton = submitButton.cloneNode(true);
            newSubmitButton.addEventListener("click", function() {
                updateUser(userId)
            })
            submitButton.replaceWith(newSubmitButton); // usuniecie starych event listenerów
        })
}

function infoForm(userId) {

    fetch(`http://localhost:9000/users/${userId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(res => res.json())
        .then(res => {
            const infoUserForm = document.getElementById("infoForm");
            document.getElementById('infoLogin').defaultValue = res.login
            document.getElementById('infoEmail').defaultValue = res.email
            document.getElementById('infoCountry').defaultValue = res.address.country
            document.getElementById('infoCity').defaultValue = res.address.city
            document.getElementById('infoStreet').defaultValue = res.address.street
            document.getElementById('infoHouseNumber').defaultValue = res.address.houseNumber
            document.getElementById('infoZipCode').defaultValue = res.address.zipCode

            infoUserForm.style.display = "block"
            const submitButton = infoUserForm.querySelector('button[type="submit"]')
        })
}

function closeForm(form) {
    document.getElementById(form).style.display = "none";
}

function validateInputs(){

    const Validation = function (value, regex, errorField) {
        this.value = value
        this.regex = regex
        this.errorField = errorField
    }

    Validation.prototype.validate = function() {
        return (this.value != null && this.value.length > 0) && this.regex.test(this.value)
    }
    Validation.prototype.showError = function() {
        this.errorField.style.visibility = "visible";
    }
    Validation.prototype.hideError = function () {
        this.errorField.style.visibility = "hidden";
    }

    const nameValidation = new Validation(
        document.getElementById("name").value,
        /^[a-zA-Z ]{1,50}$/,
        document.getElementById('nameError')
    )

    const lastNameValidation = new Validation(
        document.getElementById("lastName").value,
        /^[a-zA-Z ]{1,50}$/,
        document.getElementById('lastNameError')
    )

    const loginValidation = new Validation(
        document.getElementById("login").value,
        /^[\w.-]{0,19}[0-9a-zA-Z]$/,
        document.getElementById('loginError')
    )

    const emailValidation = new Validation(
        document.getElementById("email").value,
        /^([a-zA-Z0-9])+([.a-zA-Z0-9_-])*@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-]+)+/,
        document.getElementById('emailError')
    )

    const countryValidation = new Validation(
        document.getElementById("country").value,
        /^[a-zA-Z ]{4,56}$/,
        document.getElementById('countryError')
    )

    const cityValidation = new Validation(
        document.getElementById("city").value,
        /^[a-zA-Z]+(?:[\s-][a-zA-Z]+)*$/,
        document.getElementById('cityError')
    )

    const streetValidation = new Validation(
        document.getElementById("street").value,
        /[a-zA-Z]*/,
        document.getElementById('streetError')
    )

    const houseNumberValidation = new Validation(
        document.getElementById("houseNumber").value,
        /^[0-9]+$/,
        document.getElementById('houseNumberError')
    )

    const zipCodeValidation = new Validation(
        document.getElementById("zipCode").value,
        /^[a-z0-9][a-z0-9\- ]{0,10}[a-z0-9]$/,
        document.getElementById('zipCodeError')
    )

    const validations = [
        nameValidation,lastNameValidation,loginValidation,emailValidation,countryValidation,cityValidation,streetValidation,houseNumberValidation,zipCodeValidation
    ]

    let valid = true
    validations.forEach(validation => {
            if (validation.validate()) {
                validation.hideError()
            } else {
                validation.showError()
                valid = false
            }
        }
    )
    return valid
}

function addUser() {

    if (!validateInputs()) {
        console.log('Form is invalid')
        return
    }

    const userName = document.getElementById("name");
    const userLastName = document.getElementById("lastName");
    const userLogin = document.getElementById("login");
    const userEmail = document.getElementById("email");
    const userCountry = document.getElementById("country");
    const userCity = document.getElementById("city");
    const userStreet = document.getElementById("street");
    const userHouseNumber = document.getElementById("houseNumber");
    const userZipCode = document.getElementById("zipCode");


    fetch(`http://localhost:9000/users`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            firstName: userName.value,
            lastName: userLastName.value,
            login: userLogin.value,
            email: userEmail.value,
            address: {
                country: userCountry.value,
                city: userCity.value,
                street: userStreet.value,
                houseNumber: userHouseNumber.value,
                zipCode: userZipCode.value
            }
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => { return Promise.reject(text) })
            } else {
                return response.json();
            }
        })
        .then(user => {
            let userTableBodyElement = document.getElementById('usersBody')
            let tableHtml = userTableBodyElement.innerHTML + userRow(user)
            userTableBodyElement.innerHTML = tableHtml
        })
        .catch(error => {
            if (error === 'EMAIL_EXISTS') {
                console.log('Email exists, try different one :(')
            }
            if (error === 'LOGIN_EXISTS') {
                console.log('Login exists, try different one :(')
            }
            else {
                console.error('Error occurred', error)
            }
        })
}