import { defineEventHandler, readBody, createError, setCookie } from 'h3';
import jwt from 'jsonwebtoken';
import { db } from '../../utils/db';
import { getJwtSecret } from '../../utils/auth';

export default defineEventHandler(async (event) => {
  const body = await readBody(event);
  const { email, password } = body;

  if (!email || !password) {
    throw createError({
      statusCode: 400,
      statusMessage: 'Email e senha são obrigatórios.'
    });
  }

  try {
    const user = await db.findUserByEmail(email);
    if (!user) {
      throw createError({
        statusCode: 401,
        statusMessage: 'Esta conta não existe.'
      });
    }

    const isMatch = await db.verifyUserPassword(user, password);
    if (!isMatch) {
      throw createError({
        statusCode: 401,
        statusMessage: 'Senha incorreta.'
      });
    }

    const token = jwt.sign({ id: user.id }, getJwtSecret(), { expiresIn: '7d' });

    setCookie(event, 'jwt', token, {
      httpOnly: true,
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'lax',
      maxAge: 7 * 24 * 60 * 60 // 7 dias
    });

    return {
      success: true,
      user: {
        id: user.id,
        email: user.email,
        profiles: user.profiles,
        settings: user.settings
      }
    };
  } catch (error: any) {
    throw createError({
      statusCode: error.statusCode || 500,
      statusMessage: error.statusMessage || error.message || 'Erro ao fazer login.'
    });
  }
});
