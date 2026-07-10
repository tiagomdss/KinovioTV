import { defineEventHandler, getCookie, readBody, createError } from 'h3';
import jwt from 'jsonwebtoken';
import { db } from '../../utils/db';
import { getJwtSecret } from '../../utils/auth';

export default defineEventHandler(async (event) => {
  const token = getCookie(event, 'jwt');
  if (!token) {
    throw createError({ statusCode: 401, statusMessage: 'Não autorizado.' });
  }

  try {
    const decoded = jwt.verify(token, getJwtSecret()) as any;

    const user = await db.findUserById(decoded.id);
    if (!user) {
      throw createError({ statusCode: 404, statusMessage: 'Usuário não encontrado.' });
    }

    const body = await readBody(event);
    const { profiles, settings } = body;

    const updatedUser = await db.updateUser(decoded.id, { profiles, settings });

    return {
      success: true,
      user: {
        id: updatedUser.id,
        email: updatedUser.email,
        profiles: updatedUser.profiles,
        settings: updatedUser.settings
      }
    };
  } catch (error: any) {
    throw createError({
      statusCode: error.statusCode || 500,
      statusMessage: error.statusMessage || error.message || 'Erro ao sincronizar dados.'
    });
  }
});
