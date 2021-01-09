import React, {useState} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

import * as Store from "../store/ReduxActions";
import {Formik} from "formik";
import {Button, Form, Row, Col} from 'react-bootstrap';


function Controls({actions, character}) {

    const [errors, setErrors] = useState({});

    const onSubmit = (values, {resetForm}) => {

        values.sessionId = character.sessionId
        actions.changePlayerName(values)
            .then((res) => resetForm({}))
            .catch(() => setErrors({
                username: ["Wrong!"]
            }));
    };

    return (
        <Formik
            enableReinitialize
            initialValues={{username: character.username}}
            onSubmit={onSubmit}>
            {({
                  handleChange,
                  handleSubmit,
                  values,
                  touched
              }) => (

                <Form noValidate onSubmit={handleSubmit}>

                    <Form.Group as={Row} controlId="validationFormik01">
                        <Col sm={12}>
                            <Form.Control
                                name="username"
                                onChange={(v) => {
                                    if (errors && errors.username) {
                                        const { username, ...rest } = errors;
                                        setErrors(rest);
                                    }
                                    handleChange(v);
                                }}
                                defaultValue={values.username}
                                isValid={touched.username && !errors.username}
                                isInvalid={!!errors.username}
                                placeholder={"Username"}
                            />
                            <Form.Control.Feedback type="invalid">{errors.username}</Form.Control.Feedback>
                            <Button className={"w-100"} type="submit">Update</Button>
                        </Col>

                    </Form.Group>
                </Form>
            )}
        </Formik>
    );
}

const mapStateToProps = state => ({
    character: state.state.character
});
const mapDispatchToProps = dispatch => ({
    actions: bindActionCreators(Store, dispatch)
});
export default connect(mapStateToProps, mapDispatchToProps)(Controls);
