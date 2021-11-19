import { useState } from 'react'
import { Button, Col, Form, Modal } from 'react-bootstrap'
import LoginRequest from '../modules/LoginRequest'

interface IProps {
  show: boolean
  onClose: () => void
  onLogin: (LoginRequest: LoginRequest) => void
}

const Login = (props: IProps) => {
  const [loginDetails, setLoginDetails] = useState<LoginRequest>({
    username: '',
    password: '',
  })

  const close = () => {
    setLoginDetails({ username: '', password: '' })
    props.onClose()
  }

  return (
    <Modal show={props.show} onHide={close}>
      <Modal.Header closeButton>
        <Modal.Title>Login</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        <Form.Floating as={Col}>
          <Form.Control
            id="username-control"
            className="bg-secondary"
            placeholder="0.0"
            value={loginDetails.username}
            type="text"
            onChange={(e) =>
              setLoginDetails({ ...loginDetails, username: e.target.value })
            }
          />
          <label htmlFor="username-control">Brukernavn</label>
        </Form.Floating>

        <Form.Floating as={Col}>
          <Form.Control
            id="password-control"
            className="bg-secondary"
            placeholder="0.0"
            value={loginDetails.password}
            type="password"
            onChange={(e) =>
              setLoginDetails({ ...loginDetails, password: e.target.value })
            }
          />
          <label htmlFor="password-control">Passord</label>
        </Form.Floating>
      </Modal.Body>

      <Modal.Footer>
        <Button variant="secondary" onClick={close}>
          Close
        </Button>
        <Button
          variant="primary"
          onClick={() => {
            props.onLogin(loginDetails)
          }}
        >
          Save changes
        </Button>
      </Modal.Footer>
    </Modal>
  )
}
export default Login