import fs from 'fs/promises';
import path from 'path';
import bcrypt from 'bcrypt';

const DB_PATH = path.resolve(process.cwd(), 'server-data.json');

export interface UserProfile {
  name: string;
  avatar: string;
  favorites: any[];
  continueWatching: any[];
}

export interface UserSettings {
  stremioAddons: any[] | null;
  jellyfinServers: any[] | null;
  iptvPlaylists: any[] | null;
  debridProvider: string;
  debridToken: string;
  tmdbApiKey: string;
  playbackMode?: 'automatic' | 'manual';
}

export interface UserDoc {
  id: string;
  email: string;
  password?: string;
  profiles: UserProfile[];
  settings: UserSettings;
  createdAt: string;
}

interface DatabaseSchema {
  users: UserDoc[];
}

// Inicializar banco de dados se não existir
async function initDb(): Promise<DatabaseSchema> {
  try {
    const data = await fs.readFile(DB_PATH, 'utf-8');
    return JSON.parse(data);
  } catch (error) {
    const defaultDb: DatabaseSchema = { users: [] };
    await fs.writeFile(DB_PATH, JSON.stringify(defaultDb, null, 2), 'utf-8');
    return defaultDb;
  }
}

// Salvar dados no arquivo
async function saveDb(data: DatabaseSchema): Promise<void> {
  await fs.writeFile(DB_PATH, JSON.stringify(data, null, 2), 'utf-8');
}

export const db = {
  async getUsers(): Promise<UserDoc[]> {
    const schema = await initDb();
    return schema.users;
  },

  async findUserByEmail(email: string): Promise<UserDoc | null> {
    const users = await this.getUsers();
    return users.find(u => u.email.toLowerCase() === email.toLowerCase()) || null;
  },

  async findUserById(id: string): Promise<UserDoc | null> {
    const users = await this.getUsers();
    return users.find(u => u.id === id) || null;
  },

  async createUser(email: string, passwordPlain: string): Promise<UserDoc> {
    const schema = await initDb();
    const emailLower = email.toLowerCase();
    
    if (schema.users.some(u => u.email === emailLower)) {
      throw new Error('Este email já está cadastrado.');
    }

    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(passwordPlain, salt);

    const newUser: UserDoc = {
      id: Math.random().toString(36).substring(2, 9),
      email: emailLower,
      password: passwordHash,
      profiles: [
        { name: 'Principal', avatar: '🍿', favorites: [], continueWatching: [] }
      ],
      settings: {
        stremioAddons: null,
        jellyfinServers: null,
        iptvPlaylists: null,
        debridProvider: 'realdebrid',
        debridToken: '',
        tmdbApiKey: '',
        playbackMode: 'automatic'
      },
      createdAt: new Date().toISOString()
    };

    schema.users.push(newUser);
    await saveDb(schema);
    return newUser;
  },

  async verifyUserPassword(user: UserDoc, passwordPlain: string): Promise<boolean> {
    if (!user.password) return false;
    return bcrypt.compare(passwordPlain, user.password);
  },

  async updateUser(id: string, updates: Partial<Omit<UserDoc, 'id' | 'email' | 'password'>>): Promise<UserDoc> {
    const schema = await initDb();
    const userIdx = schema.users.findIndex(u => u.id === id);
    if (userIdx === -1) {
      throw new Error('Usuário não encontrado.');
    }

    const user = schema.users[userIdx];
    
    if (updates.profiles !== undefined) {
      user.profiles = updates.profiles;
    }
    if (updates.settings !== undefined) {
      user.settings = { ...user.settings, ...updates.settings };
    }

    schema.users[userIdx] = user;
    await saveDb(schema);
    return user;
  }
};
