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

function userRow(user){

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

function deleteUser (userId){

fetch(`http://localhost:9000/users/${userId}`, { method: 'DELETE' })
.then(() => {
  let row = document.getElementById(`user-${userId}`)
  row.remove();
})
}

function updateUser (userId) {

  const newFirstName = document.getElementById('newFirstName').value
  const newLastName = document.getElementById('newLastName').value
  const newEmail = document.getElementById('newEmail').value
  const newLogin = document.getElementById('newLogin').value

  fetch(`http://localhost:9000/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({firstName : newFirstName, lastName : newLastName, login : newLogin, email : newEmail })
  })

  .then(res => {
    console.log(res)
    if(res.ok) {
      const row = document.getElementById(`user-${userId}`)
      row.querySelector('.firstName').innerHTML = newFirstName
      row.querySelector('.lastName').innerHTML = newLastName
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
          // response = { id, firstName, lastName, login, email }
          const updateUserForm = document.getElementById("updateForm");
          const row = document.getElementById(`user-${userId}`)
          updateUserForm.querySelector('#newFirstName').value = res.firstName
          updateUserForm.querySelector('#newLastName').value = res.lastName
          updateUserForm.querySelector('#newLogin').value = res.login
          updateUserForm.querySelector('#newEmail').value = res.email
          updateUserForm.style.display = "block"
          const submitButton = updateUserForm.querySelector('button[type="submit"]')
          submitButton.addEventListener("click", function () {console.log('test')

            updateUser(userId)
          })
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
            infoUserForm.style.display = "block"
            const submitButton = infoUserForm.querySelector('button[type="submit"]')
            submitButton.addEventListener("click", function () {console.log('test')

            })
        })
}

function closeForm(form) {
  document.getElementById(form).style.display = "none";
}

function addUser(){
const userName = document.getElementById("name");
const userLastName = document.getElementById("lastName");
const userLogin = document.getElementById("login");
const userEmail = document.getElementById("email");

fetch(`http://localhost:9000/users`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    firstName : userName.value,
    lastName : userLastName.value,
    login : userLogin.value,
    email : userEmail.value
  })
})
.then(res => res.json())
.then(user => {
let userTableBodyElement = document.getElementById('usersBody')
let tableHtml = userTableBodyElement.innerHTML + userRow(user)
userTableBodyElement.innerHTML = tableHtml
})
}